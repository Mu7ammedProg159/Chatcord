package com.mdev.chatcord.client.controller.ui.main;

import com.mdev.chatcord.client.component.SpringFXMLLoader;
import com.mdev.chatcord.client.component.StageInitializer;
import com.mdev.chatcord.client.connection.ClientThread;
import com.mdev.chatcord.client.controller.ui.settings.SettingsController;
import com.mdev.chatcord.client.dto.HttpRequest;
import com.mdev.chatcord.client.implementation.EventStageHandler;
import com.mdev.chatcord.client.implementation.ThrowingRunnable;
import com.mdev.chatcord.client.implementation.UIHandler;
import com.mdev.chatcord.client.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.*;
import org.onyxfx.graphics.layout.OFxSwitcher;
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
public class NavigationBarController implements EventStageHandler, UIHandler {

    @FXML private VBox navBarContainer, navBarUserContainer;
    @FXML private ToggleButton homeBtn, chatBtn, favBtn, settingsBtn;
    @FXML private Button logoutBtn;
    @FXML private ImageView avatarImage;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private OFxSwitcher switcher;
    private Stage stage;
    private ClientThread clientThread;
    private StackPane mainOverlayPane;

    @Autowired
    private UserService userService;

    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @Autowired
    private StageInitializer stageInitializer;

    @Autowired
    private HttpRequest jwtRequest;

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


        bindImageStates(Color.web("#9F8CB2"), Color.web("#E2D7EC"), Color.web("#E2D7EC"),
                Color.web("#E2D7EC"), homeBtn, chatBtn, favBtn, settingsBtn);

    }

    public void setData(Stage stage, ClientThread clientThread){
        this.stage = stage;
        this.clientThread = clientThread;
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

        clientThread.close();
        userService.logout();
        jwtRequest.setAccessToken(null);
        stageInitializer.switchScenes(getStageActionEvent(event), "/view/login/sign-view.fxml", "Login", 1380, 750);
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

}
