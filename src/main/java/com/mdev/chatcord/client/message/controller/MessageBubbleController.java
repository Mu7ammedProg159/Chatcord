package com.mdev.chatcord.client.message.controller;

import com.mdev.chatcord.client.common.implementation.UIHandler;
import com.mdev.chatcord.client.message.dto.MessageDTO;
import com.mdev.chatcord.client.common.implementation.TimeUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class MessageBubbleController implements TimeUtils, UIHandler {

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
        username.setText(dto.getSender().getUsername());
        message.setText(dto.getContent());
        status.setText(String.valueOf(dto.getMessageStatus()));
        pfp.setImage(createImage(dto.getSender().getAvatarUrl()));

        boolean isSameSender = dto.getSender().equals(lastSender);
        boolean isLastMessageExpired = (dto.getSentAt().getSecond() - lastMessageTimestamp) > 60_000;

        String time = convertToLocalTime(dto.getSentAt());
        timestamp.setText(time);
//        Image img = new Image(getClass().getResource(dto.getProfileImageURL()).toExternalForm());
//        pfp.setImage(img);

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
}