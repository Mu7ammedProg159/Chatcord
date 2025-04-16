package com.mdev.messanger.client.controller;

import com.mdev.messanger.client.component.StageInitializer;
import com.mdev.messanger.client.connection.ClientThread;
import com.mdev.messanger.client.service.AuthService;
import com.mdev.messanger.client.service.JwtService;
import com.mdev.messanger.client.service.TokenHandler;
import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.*;

@Component
@Scope("prototype")
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

    @Autowired
    private AuthService authService;

    private final int SERVER_PORT;
    private final String SERVER_IP;

    private String username;
    private String tag;

    private ClientThread clientThread;

    public ChatController(@Value("${spring.application.udp.server.port}") int serverPort, @Value("${spring.application.udp.server.ip}") String serverIp) {
        SERVER_PORT = serverPort;
        SERVER_IP = serverIp;
    }

    @PostConstruct
    public void init() {
    }

    private void displayMessage(String receiveMessage) {
        if (!receiveMessage.isEmpty()) {
            Label msgLabel = new Label(receiveMessage);
            msgLabel.setStyle("-fx-background-color: #5865f2; -fx-text-fill: white; -fx-padding: 8; -fx-background-radius: 8;");
            messagesContainer.getChildren().add(msgLabel);
            messageField.clear();
        }
    }

    @FXML
    public void initialize() {

        String[] usernameAndTag = jwtService.validateTokenAndGetUsername(tokenHandler.getToken()).split("#");
        username = usernameAndTag[0];
        tag = usernameAndTag[1];

        chatTitle.setText("Welcome, " + username);


        contactsListView.getItems().addAll("Friend A", "Friend B", "Group 1");

        try {
            clientThread = new ClientThread(username, tag, SERVER_PORT, SERVER_IP);
            clientThread.listen(this::displayMessage);
            clientThread.sendMessage("__REGISTER__");

        } catch (SocketException e){
            throw new RuntimeException(e);
        }

        sendButton.setOnAction(e -> sendMessage());

        // Optional: scroll to bottom on new message
        messagesContainer.heightProperty().addListener((obs, oldVal, newVal) -> chatScrollPane.setVvalue(chatScrollPane.getVmax()));
    }

    private void sendMessage() {
        clientThread.sendMessage(messageField.getText());
        messageField.clear();
    }

    @FXML
    public void onLogoutClick() {
        authService.logoutUser(username, tag);
        tokenHandler.clear();
        stageInitializer.switchScenes("/view/sign-view.fxml", "Login", 800, 600);
    }
}
