package com.mdev.chatcord.client.controller.ui.main;

import com.mdev.chatcord.client.component.SpringFXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendsController {

    @FXML private Button addContactButton;
    @FXML private TextField searchField;
    @FXML private VBox contactsListView;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @FXML
    public void initialize(){

    }

    @FXML
    public void onAddContactClick(ActionEvent event) {
        try {
            FXMLLoader loader = springFXMLLoader.getLoader("/view/main-layout/addContactPopup-view.fxml");
            Parent root = loader.load();
            AddContactController controller = loader.getController();

            /*FXMLLoader contactLoader = springFXMLLoader.getLoader("/view/contact-view.fxml");
            Node contactNode = contactLoader.load();
            ContactController contactController = contactLoader.getController();
            //contactController.setData();*/

            Stage popup = new Stage();
            controller.setStage(popup);
            controller.setOnContactAdded(contactStr -> {
                Label newContact = new Label(contactStr);
                newContact.setStyle("-fx-text-fill: #202225; -fx-background-color: #1e2023");
                newContact.setOnMouseClicked(e -> loadChat(contactStr));
                contactsListView.getChildren().add(newContact);
            });

            popup.setScene(new Scene(root));
            popup.setTitle("Add Contact");
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.show();

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
