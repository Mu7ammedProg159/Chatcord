package com.mdev.chatcord.client.controller.ui;

import com.mdev.chatcord.client.component.SpringFXMLLoader;
import com.mdev.chatcord.client.component.StageInitializer;
import com.mdev.chatcord.client.connection.ClientThread;
import com.mdev.chatcord.client.connection.EMessageStatus;
import com.mdev.chatcord.client.dto.MessageDTO;
import com.mdev.chatcord.client.service.AuthService;
import com.mdev.chatcord.client.service.JwtService;
import com.mdev.chatcord.client.service.TokenHandler;
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
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @FXML
    private Button settingsBtn;

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

    private String lastMessageSender;

    private String username;
    private String tag;

    private String profileImageURL;
    private Image pfpImage;

    private String debugString;

    private final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private ClientThread clientThread;

    @Value("${spring.application.udp.server.port}") int SERVER_PORT;
    @Value("${spring.application.udp.server.ip}") String SERVER_IP;

    @PostConstruct
    public void init() {
    }

    @FXML
    public void initialize() {

        profileImageURL = "/images/pfp3.png";
        pfpImage = new Image(getClass().getResource(profileImageURL).toExternalForm());

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

            logger.info("Message Received as:  {}", dto.toString());

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
        messagesContainer.getChildren().clear();
        chatTitle.setText("Chat with " + contactId);

        // TODO: Load the chat history for this contact
        // You'll probably want a `Map<String, List<MessageDTO>>` to simulate storage
    }

    @FXML
    public void onSettingsClicked(){
        try {
        FXMLLoader loader = springFXMLLoader.getLoader("/view/settings-view.fxml");
        Parent root = loader.load();
        SettingsController controller = loader.getController();

            /*FXMLLoader contactLoader = springFXMLLoader.getLoader("/view/contact-view.fxml");
            Node contactNode = contactLoader.load();
            ContactController contactController = contactLoader.getController();
            //contactController.setData();*/

        Stage popup = new Stage();
        popup.initStyle(StageStyle.UNDECORATED);
        controller.initialize();

        popup.setScene(new Scene(root));
        popup.initModality(Modality.APPLICATION_MODAL);
        controller.setStage(popup);
        popup.show();

        logger.info("The Account Stage is " + controller.getStage());

        } catch (IOException e) {
        e.printStackTrace();
    }
    }
}
