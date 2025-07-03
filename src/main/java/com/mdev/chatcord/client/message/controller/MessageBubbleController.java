package com.mdev.chatcord.client.message.controller;

import com.mdev.chatcord.client.chat.dto.ChatMemberDTO;
import com.mdev.chatcord.client.common.implementation.UIHandler;
import com.mdev.chatcord.client.message.dto.MessageDTO;
import com.mdev.chatcord.client.common.implementation.TimeUtils;
import com.mdev.chatcord.client.message.enums.EMessageStatus;
import com.mdev.chatcord.client.message.event.OnMessageDelivered;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import lombok.Getter;
import org.onyxfx.graphics.controls.OFxAvatarView;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MessageBubbleController implements TimeUtils, UIHandler {

    @FXML @Getter
    private Label username;
    @FXML private Label message;
    @FXML private Label timestamp;
    @FXML private Label status;
    @FXML private OFxAvatarView pfp;
    @FXML private Region pfpPlaceholder;
    @FXML private VBox messageContainer;

    private ChatMemberDTO lastSender;
    private long lastMessageTimestamp;
    private int messageOffset;
    private MessageDTO content;

    public void setData(MessageDTO message) {
        this.content = message;
        username.setText(message.getSender().getUsername());
        this.message.setText(message.getContent());
        status.setText(String.valueOf(message.getMessageStatus()));

        if (message.getSender().getAvatarUrl() != null)
            pfp.setUploadedImage(createImage(message.getSender().getAvatarUrl()));
        else
            pfp.setBackgroundColor(Color.web(message.getSender().getAvatarColor()));

        boolean isSameSender = message.getSender().equals(lastSender);
        boolean isLastMessageExpired = (message.getSentAt().getSecond() - lastMessageTimestamp) > 60_000;

        String time = convertToLocalTime(message.getSentAt());
        timestamp.setText(time);

        this.message.setTextFill(Color.web("#969696D9"));
        message.setMessageStatus(EMessageStatus.UNDELIVERED);
//        Image img = new Image(getClass().getResource(message.getProfileImageURL()).toExternalForm());
//        pfp.setImage(img);

        /*// Only show PFP if sender changed
        if (!isSameSender || isLastMessageExpired) {
            if (message.getProfileImageURL() != null) {
                //setMessageElementsVisibility(true);
            }
        } else {
            //messageContainer.setStyle("-fx-padding: 10 10 2 10; -fx-background-color:  #202225;");
            System.out.println("Just Print Anything");
            //setMessageElementsVisibility(false); // hide the profile pic
        }

        lastSender = message.getUsername(); // Update the last sender
        lastMessageTimestamp = message.getTimestamp(); // Update the last message timestamp.*/

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

    @EventListener
    public void OnMessageSent(OnMessageDelivered onMessageDelivered){
        this.message.setTextFill(Color.web("#dddddd"));
        this.content.setMessageStatus(EMessageStatus.SENT);
        // You can call websocket or rest to change status of message here.
    }
}