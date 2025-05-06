package com.mdev.chatcord.client.controller.ui;

import com.mdev.chatcord.client.component.DragWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@Component
public class WindowController extends DragWindow implements EventStageHandler{

    @FXML
    private Button cancelButton, maximizeButton, minimizeButton;

    @FXML
    private HBox dragRegion;

    private double xOffset = getXOffset();
    private double yOffset = getYOffset();

    private Stage stage;

    @FXML
    private void initialize() {
        // Handle mouse press
        dragRegion.setOnMousePressed(this::handleMousePressed);

        // Handle mouse drag
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
