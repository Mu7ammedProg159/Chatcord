package com.mdev.chatcord.client.controller.ui.main.contact;

import com.mdev.chatcord.client.dto.chat.PrivateChatDTO;
import com.mdev.chatcord.client.enums.EFriendStatus;
import com.mdev.chatcord.client.implementation.UIHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class ContactController implements UIHandler {
    @FXML private ImageView contactImage;
    @FXML private Label chatName;
    @FXML private Label lastChatMessage;
    @FXML private Label timestamp;
    @FXML private Label unseenMessagesCounter;
    @FXML private Label friendStatus;

    @Getter
    @Setter
    private PrivateChatDTO privateChatDTO;

    public void setData(String name, String lastMessage, String time, int unseenCount, String contactImageURL, EFriendStatus status) {
        chatName.setText(name);
        if (lastMessage != null)
            lastChatMessage.setText(lastMessage);
        else{
            setVisibility(false, lastChatMessage);
            //lastChatMessage.setText("No messages sent yet.");
            setVisibility(true, friendStatus);
        }

        switch (status){
            case ACCEPTED -> {
                friendStatus.setText(status.name());
                friendStatus.getStyleClass().setAll("acceptedFriendStatus");
            }
            case PENDING -> {
                friendStatus.setText(status.name());
                friendStatus.getStyleClass().setAll("pendingFriendStatus");
            }
            case REQUESTED -> {
                friendStatus.setText(status.name());
                friendStatus.getStyleClass().setAll("requestedFriendStatus");
            }
            case DECLINED -> {
                friendStatus.setText(status.name());
                friendStatus.getStyleClass().setAll("declinedFriendStatus");
            }
        }


        try {
            timestamp.setText(time);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Image img = new Image(getClass().getResource(contactImageURL).toExternalForm());
        contactImage.setImage(img);

        if (unseenCount > 0) {
            unseenMessagesCounter.setText(String.valueOf(unseenCount));
            unseenMessagesCounter.setVisible(true);
        } else {
            unseenMessagesCounter.setText(String.valueOf(0));
            unseenMessagesCounter.setVisible(false);
        }
    }
}
