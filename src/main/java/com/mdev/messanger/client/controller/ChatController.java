package com.mdev.messanger.client.controller;

import com.mdev.messanger.client.component.StageInitializer;
import com.mdev.messanger.client.connection.ClientThread;
import com.mdev.messanger.client.connection.MessageDTO;
import com.mdev.messanger.client.service.AuthService;
import com.mdev.messanger.client.service.JwtService;
import com.mdev.messanger.client.service.TokenHandler;
import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.*;

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

    @Autowired
    private AuthService authService;

    private String lastMessageSender = null;

    private final int SERVER_PORT;
    private final String SERVER_IP;

    private String username;
    private String tag;

    private String profileImageURL = "/images/pfp.png";
    private Image pfpImage = new Image(getClass().getResource(profileImageURL).toExternalForm());

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

        //System.out.println(getClass().getResource("/images/pfp2.png"));


        chatTitle.setText("Welcome, " + username);


        contactsListView.getItems().addAll("Friend A", "Friend B", "Group 1");

        try {
            clientThread = new ClientThread(username, tag, SERVER_PORT, SERVER_IP);
            //clientThread.listen(this::displayMessage);
            clientThread.listen(dto -> {
                Platform.runLater(() -> {
                    Image profilePicture = new Image(getClass().getResource(dto.getProfileImageURL()).toExternalForm());
                    Node messageNode = createMessageNode(dto.getUsername(), dto.getMessage(), profilePicture);
                    messagesContainer.getChildren().add(messageNode);
                });
            });
            clientThread.sendMessage(new MessageDTO("SERVER", "__REGISTER__", "/images/pfp.png"));

        } catch (SocketException e){
            throw new RuntimeException(e);
        }

        sendButton.setOnAction(e -> sendMessage());

        // Optional: scroll to bottom on new message
        messagesContainer.heightProperty().addListener((obs, oldVal, newVal) -> chatScrollPane.setVvalue(chatScrollPane.getVmax()));
    }

    private Node createMessageNode(String username, String message, Image profileImageURL) {
        boolean isSameSenderAsLast = username.equals(lastMessageSender);
        lastMessageSender = username;

        VBox messageGroup = new VBox();
        messageGroup.setSpacing(2);

        if (!isSameSenderAsLast) {
            // Profile Image

            ImageView imageView = new ImageView(profileImageURL);
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);
            imageView.setClip(new Circle(20, 20, 20));

            // Username
            Label nameLabel = new Label(username);
            nameLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");

            HBox header = new HBox(imageView, nameLabel);
            header.setSpacing(10);
            header.setAlignment(Pos.CENTER_LEFT);

            messageGroup.getChildren().add(header);
        }

        // Message bubble
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-text-fill: #dddddd; -fx-background-color: #4f545c; -fx-padding: 8px 10px; -fx-background-radius: 8;");
        messageLabel.setMaxWidth(400);

        HBox messageBox = new HBox(messageLabel);
        messageBox.setAlignment(Pos.CENTER_LEFT);
        messageGroup.getChildren().add(messageBox);

        return messageGroup;
    }

    private void sendMessage() {
        String message = messageField.getText();

        if (!message.isEmpty()){
            MessageDTO messageDTO = new MessageDTO(username, message, profileImageURL);
            //Node messageNode = createMessageNode(username, message, pfpImage);
            //messagesContainer.getChildren().add(messageNode);

            System.out.println("--------------------- TESTING PURPOSES -----------------");
            System.out.println("--------- " + messageDTO.toString() + " ------------");
            System.out.println("--------------------- TESTING PURPOSES -----------------");

            clientThread.sendMessage(messageDTO);
            messageField.clear();
        }
    }

    @FXML
    public void onLogoutClick() {
        authService.logoutUser(username, tag);
        tokenHandler.clear();
        stageInitializer.switchScenes("/view/sign-view.fxml", "Login", 800, 600);
    }
}
