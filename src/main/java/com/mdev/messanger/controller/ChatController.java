package com.mdev.messanger.controller;

import com.mdev.messanger.component.StageInitializer;
import com.mdev.messanger.service.JwtService;
import com.mdev.messanger.service.TokenHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatController {
    @FXML
    private Label chatTitle;

    @FXML
    private ListView<String> contactsListView;

    @FXML
    private VBox messagesContainer;

    @FXML
    private ScrollPane chatScrollPane;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    @Autowired
    private TokenHandler tokenHandler;

    @Autowired
    private StageInitializer stageInitializer;

    @Autowired
    private JwtService jwtService;

    private String username;

    @FXML
    public void initialize() {
        username = jwtService.validateTokenAndGetUsername(tokenHandler.getToken());

        chatTitle.setText("Welcome, " + username);

        contactsListView.getItems().addAll("Friend A", "Friend B", "Group 1");

        sendButton.setOnAction(e -> sendMessage());

        // Optional: scroll to bottom on new message
        messagesContainer.heightProperty().addListener((obs, oldVal, newVal) ->
                chatScrollPane.setVvalue(chatScrollPane.getVmax()));
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            Label msgLabel = new Label(username + ": " + message);
            msgLabel.setStyle("-fx-background-color: #5865f2; -fx-text-fill: white; -fx-padding: 8; -fx-background-radius: 8;");
            messagesContainer.getChildren().add(msgLabel);
            messageField.clear();
        }
    }

    @FXML
    public void onLogoutClick() {
        tokenHandler.clear();
        stageInitializer.switchScenes("/view/sign-view.fxml", "Login", 800, 600);
    }
}
