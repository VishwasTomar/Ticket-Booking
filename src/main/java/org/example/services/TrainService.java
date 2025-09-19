package org.example.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.example.entities.Train;
import org.example.entities.User;
import org.example.util.UserServiceUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TrainService
{
    private List<Train> trainList;
    private ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);;

    private static final String train_Path = "C:/Users/vishw/IdeaProjects/Ticket-Booking/src/main/java/org/example/localDb/train.json";

    public TrainService() throws IOException {
        loadTrains();
    }
    public List<Train> loadTrains() throws IOException {
        File trains = new File(train_Path);
        return trainList = objectMapper.readValue(trains, new TypeReference<List<Train>>() {});
    }

    public String searchTrain(String source, String destination){
        Optional<Train> foundTrain = trainList.stream().filter(train1 ->{
            return train1.getStations().contains(source) && train1.getStations().contains(destination) && train1.getStations().indexOf(source)<train1.getStations().indexOf(destination);
        }).findFirst();
        if(foundTrain.isPresent()) {
            return foundTrain.get().getTrainId();
        }
        return "train is not present";
    }


}
