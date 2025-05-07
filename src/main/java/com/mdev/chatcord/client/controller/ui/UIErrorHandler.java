package com.mdev.chatcord.client.controller.ui;

import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public interface UIErrorHandler {

    public default void setVisibility(Node node, boolean flag){
        node.setVisible(flag);
        node.setManaged(flag);
        node.setDisable(!flag);
    }

    public default void setError(Label label, String message, TextField... fields) {
        if (label != null){
            label.setText(message);
            label.getStyleClass().removeAll("credentialLabel");
            label.getStyleClass().add("credentialLabelError");
        }

        if (fields != null){
            for (TextField field : fields) {
                field.getStyleClass().add("textFieldError");
            }
        }
    }

    public default void clearStyles(Label label, TextField... fields) {
        label.getStyleClass().removeAll("credentialLabelError");
        label.getStyleClass().add("credentialLabel");

        for (TextField field : fields) {
            field.getStyleClass().removeAll("textFieldError");
            field.getStyleClass().add("textField");
        }
    }

    public default boolean isAnyFieldEmpty(String... values) {
        for (String val : values) {
            if (val == null || val.trim().isEmpty()) return true;
        }
        return false;
    }

    public default boolean isFieldEmpty(String... values) {
        for (String val : values) {
            if (val == null || val.trim().isEmpty()) return true;
        }
        return false;
    }
}
