package com.mdev.chatcord.client.controller.ui.main;

import com.mdev.chatcord.client.component.StageInitializer;
import com.mdev.chatcord.client.connection.ClientThread;
import com.mdev.chatcord.client.implementation.ThrowingRunnable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.text.html.ImageView;

@Component
@RequiredArgsConstructor
@AllArgsConstructor
public class NavigationBarController {

    @FXML private VBox navBarContainer, navBarUserContainer;
    @FXML private Button homeBtn, chatBtn, favBtn, settingsBtn, logoutBtn;
    @FXML private ImageView avatarImage;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Getter
    @Setter
    private ThrowingRunnable runnable;

    @FXML
    public void initialize(){
        homeBtn.requestFocus();
    }

    public void switchNavBtn(Button button) throws Exception {
        if (runnable != null){
            runnable.run();
            button.requestFocus();
        }
    }

    public void onLogoutClick(Stage stage, ClientThread clientThread, StageInitializer stageInitializer) {
        //userService.logoutUser(username, tag);
        clientThread.close();
        stageInitializer.switchScenes(stage, "/view/login/sign-view.fxml", "Login", 1380, 750);
    }

}
