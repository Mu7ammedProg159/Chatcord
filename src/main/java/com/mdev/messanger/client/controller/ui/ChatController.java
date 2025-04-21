package com.mdev.messanger.client.controller.ui;

import com.mdev.messanger.client.component.SpringFXMLLoader;
import com.mdev.messanger.client.component.StageInitializer;
import com.mdev.messanger.client.connection.ClientThread;
import com.mdev.messanger.client.connection.EMessageStatus;
import com.mdev.messanger.client.dto.MessageDTO;
import com.mdev.messanger.client.service.AuthService;
import com.mdev.messanger.client.service.JwtService;
import com.mdev.messanger.client.service.TokenHandler;
import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.util.List;

@Component
public class ChatController {
    @FXML
    private Label chatTitle;

    @FXML
    private VBox contactsListView;

    @FXML
    private VBox messagesContainer;

    @FXML
    private ScrollPane chatScrollPane;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    @FXML
    private ImageView addContactButton;

    @FXML
    private Button debugger;

    @Autowired
    private TokenHandler tokenHandler;

    @Autowired
    private StageInitializer stageInitializer;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthService authService;

    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    private String lastMessageSender = null;

    private final int SERVER_PORT;
    private final String SERVER_IP;

    private String username;
    private String tag;

    private String profileImageURL = "/images/pfp3.png";
    private Image pfpImage = new Image(getClass().getResource(profileImageURL).toExternalForm());

    private String debugString;

    private ClientThread clientThread;

    public ChatController(@Value("${spring.application.udp.server.port}") int serverPort, @Value("${spring.application.udp.server.ip}") String serverIp) {
        SERVER_PORT = serverPort;
        SERVER_IP = serverIp;
    }

    @PostConstruct
    public void init() {
    }

    @FXML
    public void initialize() {

        String[] usernameAndTag = jwtService.validateTokenAndGetUsername(tokenHandler.getToken()).split("#");
        username = usernameAndTag[0];
        tag = usernameAndTag[1];

        chatTitle.setText("Welcome, " + username);
        List<Label> contactLabel;

        /*try{
            //Label contactLabel = new Label("Friend A");
            FXMLLoader loader = springFXMLLoader.getLoader("/view/contact-view.fxml");
            Node contactNode = loader.load();
            ContactController controller = loader.getController();
            controller.setData();

            contactLabel = Arrays.asList(
                    new Label("Friend A"),
                    new Label("Friend B"),
                    new Label("Friend C")
                    );

        } catch (IOException e){
            throw new RuntimeException(e);
        }

        for (Label i : contactLabel){
            contactsListView.getChildren().add(i);
        }*/

        try {
            clientThread = new ClientThread(username, tag, SERVER_PORT, SERVER_IP);
            //clientThread.listen(this::displayMessage);
            clientThread.listen(dto -> {
                Platform.runLater(() -> {
                    sentMessage(dto);
                });
            });
            clientThread.sendMessage(new MessageDTO("SERVER", "__REGISTER__", "/images/pfp.png", 0L, EMessageStatus.SENT));

        } catch (SocketException e){
            throw new RuntimeException(e);
        }

        sendButton.setOnAction(e -> sendMessage());

        // Optional: scroll to bottom on new message
        messagesContainer.heightProperty().addListener((obs, oldVal, newVal) -> chatScrollPane.setVvalue(chatScrollPane.getVmax()));
    }

    private void sentMessage(MessageDTO dto) {
        //System.out.println("DTO received: " + dto.getMessage());
        Image profilePicture = new Image(getClass().getResource(dto.getProfileImageURL()).toExternalForm());
        //Node messageNode = createMessageNode(dto.getUsername(), dto.getMessage(), profilePicture);
        try {
            FXMLLoader loader = springFXMLLoader.getLoader("/view/message-view.fxml");
            Node messageNode = loader.load();
            MessageBubbleController controller = loader.getController();
            controller.setData(dto);
            //debugString = String.valueOf(controller.getUsername());
            System.out.println("--------------------- RECEIVING -----------------------");
            System.out.println("Message Received as:  " + dto.toString());
            System.out.println("--------------------- RECEIVING -----------------------");
            messagesContainer.getChildren().add(messageNode);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage() {
        String message = messageField.getText();

        if (!message.isEmpty()){

            MessageDTO messageDTO = new MessageDTO(username, message, profileImageURL, System.currentTimeMillis(), EMessageStatus.SENT);//Node messageNode = createMessageNode(username, message, pfpImage);

            clientThread.sendMessage(messageDTO);
            messageField.clear();
        }
    }

    @FXML
    public void onDebugBtn(){
        System.out.println("DEBUG BUTTON: " + username);
    }

    @FXML
    public void onLogoutClick() {
        authService.logoutUser(username, tag);
        tokenHandler.clear();
        clientThread.close();
        stageInitializer.switchScenes("/view/sign-view.fxml", "Login", 800, 600);
    }

    @FXML
    public void onAddContactClick(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = springFXMLLoader.getLoader("/view/addContactPopup-view.fxml");
            Parent root = loader.load();
            AddContactController controller = loader.getController();

            FXMLLoader contactLoader = springFXMLLoader.getLoader("/view/contact-view.fxml");
            Node contactNode = contactLoader.load();
            ContactController contactController = contactLoader.getController();
            //contactController.setData();

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
        messagesContainer.getChildren().clear();
        chatTitle.setText("Chat with " + contactId);

        // TODO: Load the chat history for this contact
        // You'll probably want a `Map<String, List<MessageDTO>>` to simulate storage
    }
}
