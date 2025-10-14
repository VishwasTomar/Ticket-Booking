package org.example.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.Ticket;
import org.example.entities.Train;
import org.example.entities.User;

import java.io.IOException;
import java.util.*;

public class TicketService {
    private UserBookingService userBookingService;
    private TrainService trainService;
    private ObjectMapper objectMapper;

    public TicketService(UserBookingService userBookingService, TrainService trainService) {
        this.userBookingService = userBookingService;
        this.trainService = trainService;
    }

    public String bookTickets(User user, Train train, Date date, int seatsToBook, Scanner sc) throws IOException {
        Boolean seatsAvailable = checkAvailableSeats(train, seatsToBook);
        if(seatsAvailable.equals(true)){
            List<String> bookedSeats = bookAndGetSeatNumbers(train,seatsToBook);
            String ticketId = GenerateTicketId();
            String[] stations = getSourceDestinationFromUser(train, sc);
            Ticket ticket = createTicket(ticketId,user,train,stations[0],stations[1],date,bookedSeats);
            completeBooking(user,train,ticket);
            return "tickets booked successfully";
        }
        return "Booking Failed - not enough seats";
    }

    private void completeBooking(User user, Train train, Ticket ticket) throws IOException {
        if (user.getTicketsBooked() == null) {
            user.setTicketsBooked(new ArrayList<>());
        }
        user.getTicketsBooked().add(ticket);
        userBookingService.saveUserData(user);
        trainService.saveTrainData(train);
    }

    private Ticket createTicket(String ticketId, User user, Train train, String station, String station1, Date date, List<String> bookedSeats) {
        // ADD THESE DEBUG LINES:
        System.out.println("=== DEBUG TICKET CREATION ===");
        System.out.println("Ticket ID: " + ticketId);
        System.out.println("User ID: " + user.getUserId());
        System.out.println("User Name: " + user.getName());
        System.out.println("User Object: " + user);
        System.out.println("=============================");
        return new Ticket(ticketId, user.getUserId(), station, station1,date,train,bookedSeats );
    }

    private String[] getSourceDestinationFromUser(Train train, Scanner sc) {
        System.out.println("The train stops at these station "+train.getStations());
        String[] stations = new String[2];
        boolean isVaild =false;
        while(!isVaild) {
            System.out.println("Enter boarding station(source):- ");
            stations[0] = sc.next();
            System.out.println("Enter destination:- ");
            stations[1] = sc.next();
            if (train.getStations().contains(stations[0]) && train.getStations().contains(stations[1]) && train.getStations().indexOf(stations[0]) < train.getStations().indexOf(stations[1])) {
                isVaild =true;
            }else{
                System.out.println("please enter valid stations from source to destination");
            }
        }
        return stations;
    }

    private String GenerateTicketId() {
        Random random = new Random();
        return "Tkt" + (100000+ random.nextInt(900000));
    }

    private List<String> bookAndGetSeatNumbers(Train train, int seatsToBook) {
        List<String> bookedseats = new ArrayList<>();

        for(int row =0;row<train.getSeats().size();row++){
            List<Integer> currentRow  = train.getSeats().get(row);
            for(int col =0;col< currentRow.size();col++){
                if(currentRow.get(col) == 0 && bookedseats.size()<seatsToBook){
                    currentRow.set(col,1);
                    bookedseats.add(getSeatNumber(row,col));
                }
            }
        }
        return bookedseats;
    }

    private String getSeatNumber(int row, int col) {
        char rowLetter = (char) ('A'+row);
        int num  = col+1;
        return rowLetter+""+num;
    }


    private Boolean checkAvailableSeats(Train train, int seatsToBook) {
        int available = 0;
        for (int row = 0; row < train.getSeats().size(); row++) {
            List<Integer> currentRowSeats = train.getSeats().get(row);
            for (int col = 0; col < currentRowSeats.size(); col++){
                if(currentRowSeats.get(col) == 0){
                    available++;
                }
            }
        }
        return available>=seatsToBook;
    }
}
