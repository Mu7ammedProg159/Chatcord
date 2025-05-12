package com.mdev.chatcord.client.controller.ui.settings;

import com.mdev.chatcord.client.component.SpringFXMLLoader;
import com.mdev.chatcord.client.enums.ESettingStage;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.onyxfx.graphics.layout.OFxSwitcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SettingsController {

    @FXML private StackPane overlayPane;

    @FXML
    private Button menuButton, accountBtn, notificationBtn, privacyBtn, aboutBtn, cancelButton;

    @FXML
    private OFxSwitcher contentArea, navBarSwitcher;

    @FXML
    private BorderPane root;

    @FXML
    private Label emptySetting;

    private Node settingNode;

    @Getter
    @Setter
    private Stage stage;

    @Getter
    @Setter
    private Runnable onClose;

    @Autowired
    SpringFXMLLoader springFXMLLoader;

    Logger logger = LoggerFactory.getLogger(SettingsController.class);

    private ESettingStage currentSettingStage = ESettingStage.AccountStage;

    int navIndex = 0;

    public void initialize(){
        logger.info("The Account Stage is " + stage);
        contentArea.setIndex(0);
    }

    @FXML
    public void onMenuClicked(ActionEvent event){
        if (navIndex > 0)
            navIndex = 0;
        else
            navIndex++;
        navBarSwitcher.setIndex(navIndex);
    }

    @FXML
    void onAboutBtnPressed(ActionEvent event) {

    }

    @FXML
    void onAccountBtnPressed(ActionEvent event) {
        contentArea.setIndex(1);
    }

    @FXML
    void onNotificationBtnPressed(ActionEvent event) {
        /** @DEBUG THIS IS ONLY DEBUG **/
        contentArea.setIndex(0);
    }

    @FXML
    void onPrivacyBtnPressed(ActionEvent event) {

    }

    @FXML
    public void onCancel(ActionEvent e){
        close(e);
    }

    private void close(Event event) {
        if (event.getTarget() == overlayPane && onClose != null) {
            onClose.run();
        }
    }

    @FXML
    public void onClose(MouseEvent event){
       close(event);
    }
}
