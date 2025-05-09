package com.mdev.chatcord.client.controller.ui.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class AddContactController {

    @FXML private TextField usernameField;
    @FXML private TextField tagField;

    @Setter
    private Stage stage;
    @Setter
    private Consumer<String> onContactAdded;

    @FXML
    public void onAddContact(ActionEvent e) {
        String username = usernameField.getText();
        String tag = tagField.getText();

        if (!username.isEmpty() && !tag.isEmpty()) {
            onContactAdded.accept(WordUtils.capitalize(username) + "#" + tag);
            stage.close();
        }
    }

    public void onCancel(ActionEvent e) {
        stage.close();
    }
}
