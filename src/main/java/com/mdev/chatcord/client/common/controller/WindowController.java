package com.mdev.chatcord.client.common.controller;

import com.mdev.chatcord.client.annotation.DraggableWindow;
import com.mdev.chatcord.client.common.service.DragWindow;
import com.mdev.chatcord.client.common.implementation.EventStageHandler;
import com.mdev.chatcord.client.user.service.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@DraggableWindow
public class WindowController extends DragWindow implements EventStageHandler {

    @FXML
    private HBox dragRegion;

    @FXML
    private Button cancelButton, maximizeButton, minimizeButton;

    @Autowired
    private User user;

    private Stage stage;

    @FXML
    private void initialize() {
        dragRegion.setOnMousePressed(this::handleMousePressed);
        dragRegion.setOnMouseDragged(this::handleMouseDragged);
    }

    public void onCancelClicked(ActionEvent event){
        stage = getStageActionEvent(event);
//        if (user != null){
//
//        }
        stage.close();
    }

    public void onMaximizeClicked(ActionEvent event){
        stage = getStageActionEvent(event);
        stage.setMaximized(!stage.isMaximized()); // Toggles state
    }

    public void onMinimizeClicked(ActionEvent event){
        stage = getStageActionEvent(event);
        stage.setIconified(true); // Minimizes to taskbar
    }

}
