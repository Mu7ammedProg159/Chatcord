package com.mdev.chatcord.client.settings.controller;

import com.mdev.chatcord.client.common.service.DragWindow;
import com.mdev.chatcord.client.common.service.SpringFXMLLoader;
import com.mdev.chatcord.client.settings.enums.ESettingStage;
import com.mdev.chatcord.client.user.service.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.onyxfx.graphics.layout.OFxSwitcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SettingsController extends DragWindow {

    @FXML private StackPane overlayPane;

    @FXML
    private Button menuButton, accountBtn, notificationBtn, privacyBtn, aboutBtn, cancelButton;

    @FXML
    private OFxSwitcher contentArea, navBarSwitcher;

    @FXML
    private BorderPane root;

    @FXML
    private Label emptySetting;

    @FXML private HBox dragRegion;

    private Node settingNode;

    @Getter
    @Setter
    private Stage stage;

    @Getter
    @Setter
    private Runnable onClose;

    @Autowired
    SpringFXMLLoader springFXMLLoader;

    private ESettingStage currentSettingStage = ESettingStage.AccountStage;

    int navIndex = 0;


    @FXML
    public void initialize(){

        dragRegion.setOnMousePressed(this::handleMousePressed);
        dragRegion.setOnMouseDragged(this::handleMouseDragged);

    }

    public void setData(){
        log.info("The Account Stage is {}", stage);
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
    public void onCancel(ActionEvent event){
        if (onClose != null) {
            onClose.run();
        }
    }

    @FXML
    public void onClose(MouseEvent event){
        if (event.getTarget() == overlayPane && onClose != null) {
            onClose.run();
        }
    }
}
