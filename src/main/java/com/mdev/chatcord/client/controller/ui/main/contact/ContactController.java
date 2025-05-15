package com.mdev.chatcord.client.controller.ui.main.contact;

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
        if (!lastMessage.isEmpty())
            lastChatMessage.setText(lastMessage);
        else
            lastChatMessage.setText("No messages sent yet.");

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
