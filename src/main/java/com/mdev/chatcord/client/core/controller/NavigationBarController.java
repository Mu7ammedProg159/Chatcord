package com.mdev.chatcord.client.core.controller;

import com.mdev.chatcord.client.chat.events.ContactSelectedEvent;
import com.mdev.chatcord.client.common.service.SpringFXMLLoader;
import com.mdev.chatcord.client.common.service.StageInitializer;
import com.mdev.chatcord.client.connection.udp.ClientThread;
import com.mdev.chatcord.client.connection.websocket.controller.Communicator;
import com.mdev.chatcord.client.settings.controller.SettingsController;
import com.mdev.chatcord.client.common.implementation.EventStageHandler;
import com.mdev.chatcord.client.common.implementation.ThrowingRunnable;
import com.mdev.chatcord.client.common.implementation.UIHandler;
import com.mdev.chatcord.client.authentication.service.AuthenticationService;
import com.mdev.chatcord.client.token.dto.TokenFactory;
import com.mdev.chatcord.client.user.service.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.*;
import org.onyxfx.graphics.controls.OFxAvatarView;
import org.onyxfx.graphics.layout.OFxSwitcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NavigationBarController implements EventStageHandler, UIHandler {

    @FXML private VBox navBarContainer, navBarUserContainer;
    @FXML private ToggleButton homeBtn, chatBtn, favBtn, settingsBtn;
    @FXML private Button logoutBtn;
    @FXML private OFxAvatarView avatarImage;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private OFxSwitcher switcher;
    private Stage stage;
    private StackPane mainOverlayPane;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @Autowired
    private StageInitializer stageInitializer;

    @Autowired
    private TokenFactory tokenFactory;

    @Autowired
    private ClientThread clientThread;

    @Autowired
    private User user;

    @Getter
    @Setter
    private ThrowingRunnable runnable;

    @FXML
    public void initialize(){

        ToggleGroup toggleGroup = new ToggleGroup();
        homeBtn.setToggleGroup(toggleGroup);
        chatBtn.setToggleGroup(toggleGroup);
        favBtn.setToggleGroup(toggleGroup);
        settingsBtn.setToggleGroup(toggleGroup);
        avatarImage.setBackgroundColor(Color.web(user.getAvatarColor()));


        bindImageStates(Color.web("#9F8CB2"), Color.web("#E2D7EC"), Color.web("#E2D7EC"),
                Color.web("#E2D7EC"), homeBtn, chatBtn, favBtn, settingsBtn);

    }

    public void setData(Stage stage){
        this.stage = stage;
    }

    @FXML
    public void onHomeBtnClicked(ActionEvent event){
        switcher.setIndex(0);
    }

    @FXML
    public void onChatBtnClicked(ActionEvent event){
        if (switcher.getChildren().size() > 0)
            switcher.setIndex(1);
    }

    @FXML
    public void onFavBtnClicked(ActionEvent event){
        if (switcher.getChildren().size() > 1)
            switcher.setIndex(2);
    }

    @FXML
    public void onLogoutClick(ActionEvent event) {
        //userService.logoutUser(username, tag);

        try {
            clientThread.close();
            authenticationService.logout();
            tokenFactory.setAccessToken(null);
            Communicator.destroy();
            stageInitializer.switchScenes(getStageActionEvent(event), "/view/login/sign-view.fxml", "Login", 1380, 750);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onSettingsClicked(){
        try {
            FXMLLoader loader = springFXMLLoader.getLoader("/view/settings-view.fxml");
            Parent root = loader.load();
            SettingsController controller = loader.getController();

            controller.setData();

            mainOverlayPane.getChildren().add(root);

            controller.setOnClose(() -> {
                mainOverlayPane.getChildren().remove(root);
            });

            logger.info("The Account Stage is " + controller.getStage());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventListener
    public void onContactSelected(ContactSelectedEvent event){
        switcher.setIndex(1);
        chatBtn.setSelected(true);
    }

}
