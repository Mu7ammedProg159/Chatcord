package com.mdev.chatcord.client.controller.ui.main.contact;

import com.mdev.chatcord.client.component.DragWindow;
import jakarta.annotation.security.RunAs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class AddContactController extends DragWindow {

    @FXML private TextField usernameField;
    @FXML private TextField tagField;
    @FXML private HBox dragRegion;
    @FXML private StackPane overlayPane;

    @Setter
    private Stage stage;
    @Setter
    private Consumer<String> onContactAdded;

    @Getter
    @Setter
    private Runnable onClose;

    @FXML
    public void initialize(){
        dragRegion.setOnMousePressed(this::handleMousePressed);
        dragRegion.setOnMouseDragged(this::handleMouseDragged);
    }

    @FXML
    public void onAddContact(ActionEvent e) {
        String username = usernameField.getText();
        String tag = tagField.getText();

        if (!username.isEmpty() && !tag.isEmpty()) {
            onContactAdded.accept(WordUtils.capitalize(username) + "#" + tag);

        }
    }

    @FXML
    public  void onClose(MouseEvent event){
        if (event.getTarget() == overlayPane && onClose != null)
            onClose.run();
    }

    public void onCancel(ActionEvent e) {
        if (onClose != null)
            onClose.run();
    }
}
