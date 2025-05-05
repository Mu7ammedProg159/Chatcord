package com.mdev.chatcord.client.controller.ui;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public interface EventStageHandler {
    // Utility method to get the stage from the event source
    public default Stage getStageMouseEvent(MouseEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    public default Stage getStageActionEvent(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }
}
