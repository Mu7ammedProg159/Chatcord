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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
@DraggableWindow
@NoArgsConstructor
public class WindowController implements EventStageHandler{

    @FXML
    private HBox dragRegion;

    @FXML
    private Button cancelButton, maximizeButton, minimizeButton;

    private Stage stage;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @FXML
    private void initialize() {
        System.out.println("Dragging is running!.");
    }


    public void setDraggeablePressed(MouseEvent event){
        logger.info("HBOX onDrag PRESSED");
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
