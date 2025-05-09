package com.mdev.chatcord.client.controller.ui.main;

import com.mdev.chatcord.client.dto.MessageDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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
    @FXML private Region pfpPlaceholder;
    @FXML private VBox messageContainer;

    private String lastSender;
    private long lastMessageTimestamp;
    private int messageOffset;

    public void setData(MessageDTO dto) {
        username.setText(dto.getUsername());
        message.setText(dto.getMessage());
        status.setText(String.valueOf(dto.getMessageStatus()));

        boolean isSameSender = dto.getUsername().equals(lastSender);
        boolean isLastMessageExpired = (dto.getTimestamp() - lastMessageTimestamp) > 60_000;

        String time = convertToHourTime(dto.getTimestamp());
        timestamp.setText(time);
        Image img = new Image(getClass().getResource(dto.getProfileImageURL()).toExternalForm());
        pfp.setImage(img);

        /*// Only show PFP if sender changed
        if (!isSameSender || isLastMessageExpired) {
            if (dto.getProfileImageURL() != null) {
                //setMessageElementsVisibility(true);
            }
        } else {
            //messageContainer.setStyle("-fx-padding: 10 10 2 10; -fx-background-color:  #202225;");
            System.out.println("Just Print Anything");
            //setMessageElementsVisibility(false); // hide the profile pic
        }

        lastSender = dto.getUsername(); // Update the last sender
        lastMessageTimestamp = dto.getTimestamp(); // Update the last message timestamp.*/

        //message.setStyle("-fx-background-color: #5865f2; -fx-text-fill: white; -fx-padding: 8 10; -fx-background-radius: 8;");
    }

    public void setMessageElementsVisibility(boolean visibility){
        pfp.setVisible(true);
        pfp.setManaged(true);

        //pfpPlaceholder.setVisible(!visibility);
        //pfpPlaceholder.setManaged(!visibility);

        timestamp.setVisible(visibility);
        timestamp.setManaged(visibility);

        username.setVisible(visibility);
        username.setManaged(visibility);

    }

    private String convertToHourTime(long timestampMillis) {
        ZonedDateTime zoned = Instant.ofEpochMilli(timestampMillis).atZone(ZoneId.systemDefault());
        return zoned.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }
}