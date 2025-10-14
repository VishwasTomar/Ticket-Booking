package org.example;

import org.example.entities.Train;
import org.example.entities.User;
import org.example.services.TicketService;
import org.example.services.TrainService;
import org.example.services.UserBookingService;
import org.example.util.UserServiceUtil;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLOutput;
import java.util.*;

public class Main {
    private static Train selectedTrain;
    public static void main(String[] args) throws IOException {
        System.out.println("Running Train Booking System......");
        Scanner sc = new Scanner(System.in);

        UserBookingService userBookingService;
        TrainService trainService;
        try {
            userBookingService = new UserBookingService();
            trainService = new TrainService();
        } catch (IOException e) {
            System.out.println("There is something wrong "+ e);
            e.printStackTrace(); // THIS WILL SHOW THE REAL ERROR
            return;
        }
        int option =0;
        while (option!=7){
            System.out.println("choose option from 1 to 7");
            System.out.println("1. Sign Up");
            System.out.println("2. login");
            System.out.println("3. fetch Bookings");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel my booking");
            System.out.println("7. Exit from app");
            option = sc.nextInt();
            switch(option) {
                case 1:
                    System.out.println("Enter the username to signup");
                    String username = sc.next();
                    System.out.println("Enter the password to signup");
                    String password = sc.next();
                    User userToSignUp = new User(username, password, UserServiceUtil.hashPassword(password), new ArrayList<>(), UUID.randomUUID().toString());
                    System.out.println(userBookingService.signUp(userToSignUp));
                    break;

                case 2:
                    System.out.println("Enter the username to login");
                    String usernameToLogin = sc.next();
                    System.out.println("Enter the password to login");
                    String passwordToLogin = sc.next();

                    Optional<User> existingUser = userBookingService.findUserByUsername(usernameToLogin);
                    if (existingUser.isPresent()) {
                        User userToLogin = existingUser.get();

                        if (UserServiceUtil.checkPassword(passwordToLogin, userToLogin.getHashedPassword())) {
                            userBookingService = new UserBookingService(userToLogin);
                            System.out.println("Login successful!");
                        } else {
                            System.out.println("Wrong password!");
                        }
                    } else {
                        System.out.println("User not found!");
                    }
                    break;
                case 3:
                    if (userBookingService == null) {
                        System.out.println("you are not login, first log in app");
                    } else {
                        if (userBookingService.isLoggedIn()) {
                            System.out.println("fetch your bookings");
                            userBookingService.fetchBooking();
                        } else {
                            System.out.println("you are not login, first log in app");
                        }
                    }
                    break;

                case 4:
                    if (userBookingService == null) {
                        System.out.println("you are not login, first log in app");
                    } else{
                        if(userBookingService.isLoggedIn()){
                            int choice =0;
                            while(choice!=4){
                                System.out.println("1. Get all trains");
                                System.out.println("2. Search your train by trainNo.");
                                System.out.println("3. Search your train from source to destination.");
                                System.out.println("4. Exit.");
                                choice = sc.nextInt();
                                switch (choice){
                                    case 1:
                                        List<Train> trainlist = trainService.getTrainList();
                                        for(Train train : trainlist){
                                            System.out.println(train.getTrainInfo());
                                        }
                                        break;
                                    case 2:
                                        System.out.println("Enter train number");
                                        int number= sc.nextInt();;
                                        Optional<Train> train =  trainService.getTrainByNumber(String.valueOf(number));
                                        if(train.isPresent()){
                                            selectedTrain = train.get();
                                            System.out.println("Train selected: " + selectedTrain.getTrainNo());
                                            System.out.println(train.get().getTrainInfo()+" "+train.get().getStations()+" "+train.get().getSeats());
                                        }else{
                                            System.out.println("Train not found");
                                        }
                                        break;
                                    case 3:
                                        System.out.println("Enter your Source....");
                                        String source = sc.next();
                                        System.out.println("Enter your destination");
                                        String destination = sc.next();
                                        String trainId = trainService.searchTrain(source,destination);

                                        if(!trainId.equals("train is not present")){
                                            Optional<Train> foundTrain = trainService.getTrainByNumber(trainId);
                                            if(foundTrain.isPresent()){
                                                selectedTrain = foundTrain.get();
                                                System.out.println("Train selected: " + selectedTrain.getTrainNo());
                                            }
                                        } else {
                                            System.out.println("No train found for this route");
                                        }
                                        break;
                                    case 4:
                                        System.out.println("exited train search menu.");
                                        break;
                                    default:
                                        System.out.println("enter valid number from 1 to 4.");
                                        break;
                                }
                            }
                        }
                    }
                    break;
                case 5:
                    if (userBookingService == null || !userBookingService.isLoggedIn()) {
                        System.out.println("Please login first!");
                    } else if (selectedTrain == null) {
                        System.out.println("Please search and select a train first from Option 4");
                    } else {
                        try {
                            System.out.println("Booking seats for train: " + selectedTrain.getTrainNo());

                            // Get travel date from user
                            System.out.println("Enter travel date (yyyy-mm-dd):");
                            String dateStr = sc.next();
                            Date travelDate = java.sql.Date.valueOf(dateStr);

                            // Get number of seats
                            System.out.println("Enter number of seats to book:");
                            int seatsToBook = sc.nextInt();

                            // Get the logged-in user
                            User currentUser = userBookingService.getCurrentUser();

                            // Create TicketService and book tickets
                            TicketService ticketService = new TicketService(userBookingService, trainService);
                            String result = ticketService.bookTickets(currentUser, selectedTrain, travelDate, seatsToBook, sc);

                            System.out.println(result);

                        } catch (Exception e) {
                            System.out.println("Booking failed: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    break;
            }

        }
    }
}