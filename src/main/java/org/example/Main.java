package org.example;

import org.example.entities.User;
import org.example.services.UserBookingService;
import org.example.util.UserServiceUtil;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        System.out.println("Running Train Booking System......");
        Scanner sc = new Scanner(System.in);

        UserBookingService userBookingService;
        try {
            userBookingService = new UserBookingService();
        } catch (IOException e) {
            System.out.println("There is something wrong");
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
            switch(option){
                case 1:
                    System.out.println("Enter the username to signup");
                    String username = sc.next();
                    System.out.println("Enter the password to signup");
                    String password = sc.next();
                    User userToSignUp = new User(username,password, UserServiceUtil.hashPassword(password),new ArrayList<>(), UUID.randomUUID().toString());
                    System.out.println(userBookingService.signUp(userToSignUp));
                    break;

                case 2:
                    System.out.println("Enter the username to login");
                    String usernameToLogin = sc.next();
                    System.out.println("Enter the password to login");
                    String passwordToLogin = sc.next();
                    User userToLogin = new User(usernameToLogin,passwordToLogin, UserServiceUtil.hashPassword(passwordToLogin));
                    try{
                        userBookingService = new UserBookingService(userToLogin);
                        System.out.println(userBookingService.loginUser());
                    }catch(IOException ex){
                        ex.printStackTrace();
                        return;
                    }

                    break;
                case 3:
                    if(userBookingService != null && userBookingService.isLoggedIn()) {
                        System.out.println("fetch your bookings");
                        userBookingService.fetchBooking();
                    }else{
                        System.out.println("you are not login, first log in app");
                    }
                case 4:

                case 7:
                    System.out.println("exit from App.");
                    break;

            }

        }
    }
}