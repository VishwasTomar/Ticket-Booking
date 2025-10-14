package org.example.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.example.entities.Ticket;
import org.example.entities.Train;
import org.example.entities.User;
import org.example.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserBookingService {
    private User user;
    private List<User> userList;
    private TrainService trainService;
    private ObjectMapper objectMapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    private static final String USERS_PATH =  "C:/Users/vishw/IdeaProjects/Ticket-Booking/src/main/java/org/example/localDb/users.json";
    public UserBookingService(User user, TrainService trainService) throws IOException {
        this.trainService = trainService;
        loadUsers();

        Optional<User> completeUser = userList.stream()
                .filter(u -> u.getName().equalsIgnoreCase(user.getName()))
                .findFirst();

        if (completeUser.isPresent()) {
            this.user = completeUser.get(); // Use user from JSON with tickets
        } else {
            this.user = user;
            System.out.println("User not found in database!");
        }
    }

    public UserBookingService(TrainService trainService) throws IOException {
        this.trainService = trainService;
        loadUsers();
    }
    public UserBookingService() throws IOException {
        loadUsers();
    }


    public List<User> loadUsers() throws IOException {
        File users = new File(USERS_PATH);
        return userList = objectMapper.readValue(users, new TypeReference<List<User>>() {});
    }

    public Boolean loginUser(){
        if (user == null){
            return false;
        }
        Optional<User> foundUser = userList.stream().filter(user1 ->{
            return user1.getName().equalsIgnoreCase(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        return foundUser.isPresent();
    }
     public boolean isLoggedIn(){
        return loginUser();
     }

    public String signUp(User user){
        try{
            userList.add(user);
            saveUserListToFile();
            return String.format("%s signed up successfully.",user.getName());
            //return Boolean.TRUE;
        }catch(IOException ex){
            return String.format("sign up failed.");
            //return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException {
        File userFile = new File(USERS_PATH);
        objectMapper.writeValue(userFile, userList);

    }
    // json file -> object(user) -> deserialize
    // object(user) -> json file -> serialize
    public void fetchBooking(){
        /*System.out.println("=== DEBUG: fetchBooking() called ===");
        System.out.println("User: " + (user != null ? user.getName() : "NULL USER"));
        System.out.println("TicketsBooked: " + (user != null ? user.getTicketsBooked() : "NO USER"));
        if (user != null && user.getTicketsBooked() != null) {
            System.out.println("Ticket count: " + user.getTicketsBooked().size());
        } else {
            System.out.println("Either user is null or ticketsBooked is null");
        }
        System.out.println("===================================");*/
        user.printTickets();
    }

    public Optional<User> findUserByUsername(String username) {
        return userList.stream()
                .filter(user -> user.getName().equalsIgnoreCase(username))
                .findFirst();
    }
    //implement cancelBooking that i will do after watching once again video
    public boolean cancelBooking(String ticketId){
        if (trainService == null) {
            System.out.println("ERROR: trainService is null in cancelBooking!");
            return false;
        }
        // 1. Find user
        Optional<User> foundUser = userList.stream()
                .filter(u -> u.getName().equalsIgnoreCase(user.getName()))
                .findFirst();

        if (!foundUser.isPresent()) return false;

        User currentUser = foundUser.get();

        // 2. Find ticket FIRST (before removing)
        Optional<Ticket> ticketToCancel = currentUser.getTicketsBooked().stream()
                .filter(ticket -> ticket.getTicketId().equals(ticketId))
                .findFirst();

        if (!ticketToCancel.isPresent()) return false;

        Ticket cancelledTicket = ticketToCancel.get();

        // 3. Remember train and seats
        Train train = cancelledTicket.getTrain();
        List<String> bookedSeats = cancelledTicket.getBookedSeats();

        // 4. Remove ticket
        boolean removed = currentUser.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(ticketId));

        if (removed) {
            try {
                System.out.println("DEBUG: Booked seats list for ticket " + ticketId + " is: " + bookedSeats);
                // 5. Free up seats
                freeUpTrainSeats(train, bookedSeats);

                // 6. Save both files
                saveUserListToFile();

                trainService.saveTrainData(train);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public TrainService getTrainService() {
        return trainService;
    }

    public void setTrainService(TrainService trainService) {
        this.trainService = trainService;
    }

    private void freeUpTrainSeats(Train train, List<String> bookedSeats) {
        // Add comprehensive null checks
        if (train == null) {
            System.out.println("ERROR: Train object is null.");
            return;
        }

        if (train.getSeats() == null) {
            System.out.println("ERROR: Train seat matrix is null.");
            return;
        }

        if (bookedSeats == null || bookedSeats.isEmpty()) {
            System.out.println("INFO: No booked seats to free.");
            return;
        }

        // Now proceed with freeing the seats
        for (String seatCode : bookedSeats) {
            int row = seatCode.charAt(0) - 'A';
            int col = Integer.parseInt(seatCode.substring(1)) - 1;

            // Optional: Check if row and col are within the seat matrix bounds
            if (row < train.getSeats().size() && col < train.getSeats().get(row).size()) {
                train.getSeats().get(row).set(col, 0); // Free the seat
            } else {
                System.out.println("WARNING: Seat code " + seatCode + " is invalid for this train.");
            }
        }
        System.out.println("Seats successfully freed.");
    }
    public void saveUserData(User user) throws IOException {
        File userFile = new File(USERS_PATH);
        objectMapper.writeValue(userFile,userList);
    }

    public User getCurrentUser() {
        return this.user;
    }
}
