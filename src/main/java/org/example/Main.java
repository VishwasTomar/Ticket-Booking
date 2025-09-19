package org.example;

import org.example.entities.Train;
import org.example.entities.User;
import org.example.services.TrainService;
import org.example.services.UserBookingService;
import org.example.util.UserServiceUtil;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;

public class Main {
    private static Train selectedTrain;
    public static void main(String[] args) {
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
                    User userToLogin = new User(usernameToLogin, passwordToLogin, UserServiceUtil.hashPassword(passwordToLogin));
                    try {
                        userBookingService = new UserBookingService(userToLogin);
                        System.out.println(userBookingService.loginUser());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        return;
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
                                        System.out.println(trainService.searchTrain(source,destination));
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
                    System.out.println("Book your tickets");
                    break;
            }

        }
    }
}