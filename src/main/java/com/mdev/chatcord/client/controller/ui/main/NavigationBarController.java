package com.mdev.chatcord.client.controller.ui.main;

import com.mdev.chatcord.client.component.SpringFXMLLoader;
import com.mdev.chatcord.client.component.StageInitializer;
import com.mdev.chatcord.client.connection.ClientThread;
import com.mdev.chatcord.client.controller.ui.settings.SettingsController;
import com.mdev.chatcord.client.implementation.EventStageHandler;
import com.mdev.chatcord.client.implementation.ThrowingRunnable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
public class NavigationBarController implements EventStageHandler {

    @FXML private VBox navBarContainer, navBarUserContainer;
    @FXML private Button homeBtn, chatBtn, favBtn, settingsBtn, logoutBtn;
    @FXML private ImageView avatarImage;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private OFxSwitcher switcher;
    private Stage stage;
    private ClientThread clientThread;
    private StackPane mainOverlayPane;

    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @Autowired
    private StageInitializer stageInitializer;

    @Getter
    @Setter
    private ThrowingRunnable runnable;

    @FXML
    public void initialize(){
        homeBtn.requestFocus();
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
        stageInitializer.switchScenes(getStageActionEvent(event), "/view/login/sign-view.fxml", "Login", 1380, 750);
    }

    @FXML
    public void onSettingsClicked(){
        try {
            FXMLLoader loader = springFXMLLoader.getLoader("/view/settings-view.fxml");
            Parent root = loader.load();
            SettingsController controller = loader.getController();

            controller.initialize();

            mainOverlayPane.getChildren().add(root);


            controller.setOnClose(() -> {
                mainOverlayPane.getChildren().remove(root);
                homeBtn.requestFocus();
                switcher.setIndex(0);
            });

            logger.info("The Account Stage is " + controller.getStage());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
