package com.mdev.chatcord.client.controller.ui.settings;

import com.mdev.chatcord.client.component.SpringFXMLLoader;
import com.mdev.chatcord.client.enums.ESettingStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SettingsController {
    @FXML
    private Button menuButton, accountBtn, notificationBtn, privacyBtn, aboutBtn, cancelButton;

    @FXML
    private StackPane contentArea;

    @FXML
    private BorderPane root;

    @FXML
    private Label emptySetting;

    private Node settingNode;

    @Setter
    @Getter
    private Stage stage;

    @Autowired
    SpringFXMLLoader springFXMLLoader;

    Logger logger = LoggerFactory.getLogger(SettingsController.class);

    private ESettingStage currentSettingStage = ESettingStage.AccountStage;

    public void initialize(){
        logger.info("The Account Stage is " + stage);


        try{
            switchSettingStage(currentSettingStage);

        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

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

    public void switchSettingStage(ESettingStage eSettingStage) throws IOException {

        switch (eSettingStage){
            case AccountStage -> {
                FXMLLoader loader = springFXMLLoader.getLoader("/view/settings/settings-account-view.fxml");
                settingNode = loader.load();
                AccountSettingsController controller = loader.getController();
                //To-do: make a method in account setting to fetch data from a user service or controller.
                contentArea.getChildren().clear();
                contentArea.getChildren().add(settingNode);
            }
            case NotificationStage -> {

            }
            case PrivacyStage -> {

            }
            case AboutStage -> {

            }

            default -> {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(emptySetting);
            }
        }

    }

    @FXML
    public void onCancel(ActionEvent e){
        stage.close();
    }
}
