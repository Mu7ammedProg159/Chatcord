package com.mdev.messanger.client.controller;

import com.mdev.messanger.client.connection.MessageDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Component
public class MessageBubbleController {

    @FXML @Getter
    private Label username;
    @FXML private Label message;
    @FXML private Label timestamp;
    @FXML private Label status;
    @FXML private ImageView pfp;

    public void setData(MessageDTO dto) {
        username.setText(dto.getUsername());
        message.setText(dto.getMessage());

        String time = convertToHourTime(dto.getTimestamp());
        timestamp.setText(time);

        if (dto.getProfileImageURL() != null) {
            Image img = new Image(getClass().getResource(dto.getProfileImageURL()).toExternalForm());
            pfp.setImage(img);
        }

        System.out.println("--------------------- TESTING PURPOSES BUBBLE -----------------");
        System.out.println("--------- " + dto.toString() + " ------------");
        System.out.println("--------------------- TESTING PURPOSES BUBBLE -----------------");

        message.setStyle("-fx-background-color: #5865f2; -fx-text-fill: white; -fx-padding: 8 10; -fx-background-radius: 8;");
        //if (dto.getUsername().equalsIgnoreCase("SERVER")) { }
    }

    private String convertToHourTime(long timestampMillis) {
        ZonedDateTime zoned = Instant.ofEpochMilli(timestampMillis).atZone(ZoneId.systemDefault());
        return zoned.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }
}