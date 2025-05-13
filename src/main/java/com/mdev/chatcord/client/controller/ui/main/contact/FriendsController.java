package com.mdev.chatcord.client.controller.ui.main.contact;

import com.mdev.chatcord.client.component.SpringFXMLLoader;
import com.mdev.chatcord.client.controller.ui.main.ChatController;
import com.mdev.chatcord.client.implementation.TimeUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendsController implements TimeUtil {

    @FXML private Button addContactButton;
    @FXML private TextField searchField;
    @FXML private VBox contactsListView;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    private StackPane mainOverlayPane;

    @FXML
    public void initialize(){

    }

    @FXML
    public void onAddContactClick(ActionEvent event) {
        try {
            FXMLLoader loader = springFXMLLoader.getLoader("/view/main-layout/addContactPopup-view.fxml");
            Parent root = loader.load();
            AddContactController controller = loader.getController();

            mainOverlayPane.getChildren().add(root);

            controller.setOnContactAdded(contactStr -> {

                FXMLLoader contactLoader = springFXMLLoader.getLoader("/view/main-layout/contact-view.fxml");
                Parent newContact;
                try {
                    newContact = loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ContactController contactController = loader.getController();

                contactController.setData(contactStr, null, convertToHourTime(System.currentTimeMillis()),
                        0, "/images/default_pfp.png");

                newContact.setOnMouseClicked(e -> loadChat(contactStr));
                contactsListView.getChildren().add(newContact);
            });

            controller.setOnClose(() -> {
                mainOverlayPane.getChildren().remove(root);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadChat(String contactId) {

        FXMLLoader loader = springFXMLLoader.getLoader("/view/main-layout/chat-view.fxml");
        try {
            Parent root = loader.load();
            ChatController controller = loader.getController();

            controller.getMessagesContainer().getChildren().clear();
            controller.getChatTitle().setText("Chat with " + contactId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // TODO: Load the chat history for this contact
        // You'll probably want a `Map<String, List<MessageDTO>>` to simulate storage
    }

}
