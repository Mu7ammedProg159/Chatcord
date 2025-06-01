package com.mdev.chatcord.client.core.controller;

import com.mdev.chatcord.client.chat.direct.controller.ChatController;
import com.mdev.chatcord.client.common.service.SpringFXMLLoader;
import com.mdev.chatcord.client.common.service.StageInitializer;
import com.mdev.chatcord.client.friend.controller.FriendsController;
import com.mdev.chatcord.client.settings.controller.SettingsController;
import com.mdev.chatcord.client.user.dto.ProfileDetails;
import com.mdev.chatcord.client.common.implementation.UIHandler;
import com.mdev.chatcord.client.user.service.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onyxfx.graphics.layout.OFxSwitcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
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

    private final StageInitializer stageInitializer;

    private final User userDetails;

    private final SpringFXMLLoader springFXMLLoader;

    @Autowired
    private ChatController chatController;

    private final NavigationBarController navigationBarController;
    private final FriendsController friendsController;
    private final SettingsController settingsController;

    @FXML
    public void initialize() {

        username = userDetails.getUsername();
        tag = userDetails.getTag();

        windowBarBtns.getChildren().get(2).getStyleClass().add("onCancelRound");
        changeFont(appName, "/fonts/CarterOne-Regular.ttf", 20);

        initializeControllers();
         //clientThread.listen(this::displayMessage);

        //chatController.sendMessage("SERVER", "Community", "");

        navigationBarController.setData(stageInitializer.getPrimaryStage());

        // Scroll to bottom on new message
//        chatController.getMessagesContainer().heightProperty().addListener(
//                (obs, oldVal, newVal) ->
//                        chatController.getChatScrollPane().setVvalue(
//                                chatController.getChatScrollPane().getVmax()));
    }

    private void initializeControllers() {
        chatController.setAvatarImage(createImage(userDetails.getPfpUrl()));
        navigationBarController.setSwitcher(switcher);
        navigationBarController.getHomeBtn().setSelected(true);
        navigationBarController.getSwitcher().setIndex(0);
        navigationBarController.setMainOverlayPane(overlayPane);
        friendsController.setMainOverlayPane(overlayPane);
    }

}
