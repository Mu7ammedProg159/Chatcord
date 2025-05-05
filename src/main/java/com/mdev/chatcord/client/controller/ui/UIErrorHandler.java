package com.mdev.chatcord.client.controller.ui;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public interface UIErrorHandler {
    public default void setError(Label label, String message, TextField... fields) {
        label.setText(message);
        label.getStyleClass().removeAll();
        label.getStyleClass().add("credentialLabelError");

        for (TextField field : fields) {
            field.getStyleClass().add("textFieldError");
        }
    }

    public default void clearStyles(Label label, TextField... fields) {
        label.getStyleClass().removeAll();
        label.getStyleClass().add("credentialLabel");

        for (TextField field : fields) {
            field.getStyleClass().removeAll();
            field.getStyleClass().add("text-field");
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
