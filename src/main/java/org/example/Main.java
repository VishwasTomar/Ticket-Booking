package org.example;

import org.example.services.UserBookingService;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Running Train Booking System......");
        Scanner sc = new Scanner(System.in);

        UserBookingService userBookingService;
        try {
            userBookingService = new UserBookingService();
        } catch (IOException e) {
            System.out.println("There is something wrong");
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
            System.out.println("7. Exit");
            option = sc.nextInt();

        }
    }
}