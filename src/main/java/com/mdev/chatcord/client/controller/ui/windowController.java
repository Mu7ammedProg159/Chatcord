package com.mdev.chatcord.client.controller.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;


@Component
public class windowController implements EventStageHandler{

    @FXML
    private Button cancelButton, maximizeButton, minimizeButton;

    @FXML
    private HBox dragRegion;

    private double xOffset = 0;
    private double yOffset = 0;

    private Stage stage;

    @FXML
    private void initialize() {
        // Handle mouse press
        dragRegion.setOnMousePressed(this::handleMousePressed);

        // Handle mouse drag
        dragRegion.setOnMouseDragged(this::handleMouseDragged);
    }

    // Mouse pressed handler to capture initial mouse position
    private void handleMousePressed(MouseEvent event) {
        // Capture current position of the mouse relative to the screen
        xOffset = event.getScreenX() - getStageMouseEvent(event).getX();
        yOffset = event.getScreenY() - getStageMouseEvent(event).getY();
    }

    // Mouse dragged handler to move the window
    private void handleMouseDragged(MouseEvent event) {
        Stage stage = getStageMouseEvent(event);

        // Move the window based on the current mouse position
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
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
