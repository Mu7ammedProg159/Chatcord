package com.mdev.chatcord.client.controller.ui;

import com.mdev.chatcord.client.annotation.DraggableWindow;
import com.mdev.chatcord.client.component.DragWindow;
import com.mdev.chatcord.client.implementation.EventStageHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;


@Component
@DraggableWindow
public class WindowController extends DragWindow implements EventStageHandler {

    @FXML
    private HBox dragRegion;

    @FXML
    private Button cancelButton, maximizeButton, minimizeButton;

    private Stage stage;

    @FXML
    private void initialize() {
        dragRegion.setOnMousePressed(this::handleMousePressed);
        dragRegion.setOnMouseDragged(this::handleMouseDragged);
    }

    public void onCancelClicked(ActionEvent event){
        stage = getStageActionEvent(event);
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
