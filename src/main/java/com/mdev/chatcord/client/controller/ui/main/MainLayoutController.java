package com.mdev.chatcord.client.controller.ui.main;

import com.mdev.chatcord.client.component.SpringFXMLLoader;
import com.mdev.chatcord.client.component.StageInitializer;
import com.mdev.chatcord.client.connection.ClientThread;
import com.mdev.chatcord.client.controller.ui.main.contact.FriendsController;
import com.mdev.chatcord.client.controller.ui.settings.SettingsController;
import com.mdev.chatcord.client.enums.EMessageStatus;
import com.mdev.chatcord.client.dto.HttpRequest;
import com.mdev.chatcord.client.dto.MessageDTO;
import com.mdev.chatcord.client.implementation.UIHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import lombok.RequiredArgsConstructor;
import org.onyxfx.graphics.layout.OFxSwitcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;

@Component
@RequiredArgsConstructor
public class MainLayoutController implements UIHandler {

    @FXML
    private HBox windowBarBtns;

    @FXML
    private StackPane overlayPane;

    @FXML
    private Label appName;

    @FXML
    private OFxSwitcher switcher;

    private String username;
    private String tag;
    private String lastMessageSender;
    private String profileImageURL;
    private Image pfpImage;

    private String debugString;
    private ClientThread clientThread;

    private final Logger logger = LoggerFactory.getLogger(MainLayoutController.class);

    private final StageInitializer stageInitializer;

    private final HttpRequest jwtRequest;

    private final SpringFXMLLoader springFXMLLoader;

    private final NavigationBarController navigationBarController;
    private final FriendsController friendsController;
    private final SettingsController settingsController;

    @Autowired
    private ChatController chatController;

    @Value("${spring.application.udp.server.port}") int SERVER_PORT;
    @Value("${spring.application.udp.server.ip}") String SERVER_IP;

    @FXML
    public void initialize() {

        profileImageURL = "/images/pfp3.png";
        pfpImage = createImage(profileImageURL);

        username = jwtRequest.getUserDTO().getUsername();
        tag = jwtRequest.getUserDTO().getTag();

        windowBarBtns.getChildren().get(2).getStyleClass().add("onCancelRound");
        changeFont(appName, "/fonts/CarterOne-Regular.ttf", 20);

        initializeControllers();

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

        navigationBarController.setData(stageInitializer.getPrimaryStage(), clientThread);

        chatController.getSendBtn().setOnAction(e -> chatController.sendMessage(clientThread, username, profileImageURL));

        // Optional: scroll to bottom on new message
        chatController.getMessagesContainer().heightProperty().addListener(
                (obs, oldVal, newVal) ->
                        chatController.getChatScrollPane().setVvalue(
                                chatController.getChatScrollPane().getVmax()));
    }

    private void initializeControllers() {
        chatController.setData(username, tag);
        navigationBarController.setSwitcher(switcher);
        navigationBarController.getHomeBtn().setSelected(true);
        navigationBarController.getSwitcher().setIndex(0);
        navigationBarController.setMainOverlayPane(overlayPane);
        friendsController.setMainOverlayPane(overlayPane);
    }

    private void sentMessage(MessageDTO dto) {
        //System.out.println("DTO received: " + dto.getMessage());
        Image profilePicture = createImage(dto.getProfileImageURL());
        //Node messageNode = createMessageNode(dto.getUsername(), dto.getMessage(), profilePicture);
        try {
            FXMLLoader loader = springFXMLLoader.getLoader("/view/main-layout/message-view.fxml");
            Node messageNode = loader.load();
            MessageBubbleController controller = loader.getController();
            controller.setData(dto);
            //debugString = String.valueOf(controller.getUsername());

            logger.info("Message Received as:  {}", dto.toString());

            chatController.addMessage(messageNode);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onDebugBtn(){
        System.out.println("DEBUG BUTTON: " + username);
    }

}
