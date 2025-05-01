package com.mdev.chatcord.client.controller.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.stereotype.Component;

@Component
public class ContactController {
    @FXML private ImageView contactImage;
    @FXML private Label chatName;
    @FXML private Label lastChatMessage;
    @FXML private Label timestamp;
    @FXML private Label unseenMessagesCounter;

    public void setData(String name, String lastMessage, String time, int unseenCount, String contactImageURL) {
        chatName.setText(name);
        lastChatMessage.setText(lastMessage);
        timestamp.setText(time);
        Image img = new Image(getClass().getResource(contactImageURL).toExternalForm());
        contactImage.setImage(img);

        if (unseenCount > 0) {
            unseenMessagesCounter.setText(String.valueOf(unseenCount));
            unseenMessagesCounter.setVisible(true);
            unseenMessagesCounter.setManaged(true);
        } else {
            unseenMessagesCounter.setVisible(false);
            unseenMessagesCounter.setManaged(false);
        }
    }
}
