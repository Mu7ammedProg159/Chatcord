package com.mdev.chatcord.client.controller.ui;

import com.mdev.chatcord.client.annotation.DraggableWindow;
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
@DraggableWindow
public class WindowController implements EventStageHandler{

    @FXML
    private HBox dragRegion;

    @FXML
    private Button cancelButton, maximizeButton, minimizeButton;

    private Stage stage;

    @FXML
    private void initialize() {

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
