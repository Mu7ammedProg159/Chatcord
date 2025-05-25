package com.mdev.chatcord.client.controller.ui.main;

import com.mdev.chatcord.client.component.SpringFXMLLoader;
import com.mdev.chatcord.client.component.StageInitializer;
import com.mdev.chatcord.client.connection.ClientThread;
import com.mdev.chatcord.client.controller.ui.main.contact.FriendsController;
import com.mdev.chatcord.client.controller.ui.settings.SettingsController;
import com.mdev.chatcord.client.dto.HttpRequest;
import com.mdev.chatcord.client.dto.Profile;
import com.mdev.chatcord.client.implementation.UIHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
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

    private final ClientThread clientThread;

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

    private Profile userDetails;

    @FXML
    public void initialize() {

        userDetails = jwtRequest.getUserDTO();

        profileImageURL = "/images/pfp2.jpg";
        pfpImage = createImage(profileImageURL);

        username = userDetails.getUsername();
        tag = userDetails.getTag();

        windowBarBtns.getChildren().get(2).getStyleClass().add("onCancelRound");
        changeFont(appName, "/fonts/CarterOne-Regular.ttf", 20);

        initializeControllers();
         //clientThread.listen(this::displayMessage);

        clientThread.listen(dto -> {
            Platform.runLater(() -> {
                chatController.createReceiveMessage(dto, pfpImage);
            });
        });

        chatController.sendMessage("SERVER", "Community", "");

        navigationBarController.setData(stageInitializer.getPrimaryStage());

        // Scroll to bottom on new message
        chatController.getMessagesContainer().heightProperty().addListener(
                (obs, oldVal, newVal) ->
                        chatController.getChatScrollPane().setVvalue(
                                chatController.getChatScrollPane().getVmax()));
    }

    private void initializeControllers() {
        chatController.setAvatarImage(createImage(userDetails.getPfpUrl()));
        navigationBarController.setSwitcher(switcher);
        navigationBarController.getHomeBtn().setSelected(true);
        navigationBarController.getSwitcher().setIndex(0);
        navigationBarController.setMainOverlayPane(overlayPane);
        friendsController.setMainOverlayPane(overlayPane);
    }

    @FXML
    public void onDebugBtn(){
        System.out.println("DEBUG BUTTON: " + username);
    }

}
