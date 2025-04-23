package com.mdev.messanger.client.controller.ui;

import com.mdev.messanger.client.component.SpringFXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SettingsController {
    @FXML
    private Button aboutBtn;

    @FXML
    private Button accountBtn;

    @FXML
    private StackPane contentArea;

    @FXML
    private Button menuButton;

    @FXML
    private Button notificationBtn;

    @FXML
    private Button privacyBtn;

    @FXML
    private BorderPane root;

    @Autowired
    SpringFXMLLoader springFXMLLoader;

    private ESettingStage currentSettingStage = ESettingStage.AccountStage;

    @FXML
    void onAboutBtnPressed(ActionEvent event) {

    }

    @FXML
    void onAccountBtnPressed(ActionEvent event) {

    }

    @FXML
    void onNotificationBtnPressed(ActionEvent event) {

    }

    @FXML
    void onPrivacyBtnPressed(ActionEvent event) {

    }

    public void switchSettingStage(ESettingStage eSettingStage){

        switch (eSettingStage){
            case AccountStage -> {
                contentArea.getChildren().add();
            }
            case NotificationStage -> {

            }
            case PrivacyStage -> {

            }
            case AboutStage -> {

            }
        }

    }
}
