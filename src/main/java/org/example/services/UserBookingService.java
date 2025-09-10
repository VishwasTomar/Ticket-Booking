package org.example.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.example.entities.Ticket;
import org.example.entities.User;
import org.example.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserBookingService {
    private User user;
    private List<User> userList;
    private ObjectMapper objectMapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    private static final String USERS_PATH =  "C:/Users/vishw/IdeaProjects/Ticket-Booking/src/main/java/org/example/localDb/users.json";
    public UserBookingService(User user1) throws IOException {
        this.user = user1;
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
        Optional<User> foundUser = userList.stream().filter(user1 ->{
            return user1.getName().equalsIgnoreCase(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        return foundUser.isPresent();
    }

    public Boolean signUp(User user){
        try{
            userList.add(user);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch(IOException ex){
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException {
        File userFile = new File(USERS_PATH);
        objectMapper.writeValue(userFile, userList);

    }
    // json file -> object(user) -> deserialize
    // object(user) -> json file -> serialize
    public void fetchBooking(){
        user.printTickets();
    }

    //implement cancelBooking that i will do after watching once again video
    public boolean cancelBooking(String ticketId){
        Optional<User> foundUser = userList.stream()
                .filter(u -> u.getName().equalsIgnoreCase(user.getName()))
                .findFirst();

        if (!foundUser.isPresent()) {
            return false; // user not found
        }

        User currentUser = foundUser.get();

        // Find ticket by id
        boolean removed = currentUser.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(ticketId));

        if (removed) {
            try {
                saveUserListToFile(); // persist changes
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false; // ticket not found
    }

}
