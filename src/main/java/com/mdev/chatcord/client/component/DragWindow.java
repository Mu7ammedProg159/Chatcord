package com.mdev.chatcord.client.component;


import com.mdev.chatcord.client.controller.ui.EventStageHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DragWindow implements EventStageHandler {
    private double xOffset;
    private double yOffset;

    // Mouse pressed handler to capture initial mouse position
    public void handleMousePressed(MouseEvent event) {
        // Capture current position of the mouse relative to the screen
        xOffset = event.getScreenX() - getStageMouseEvent(event).getX();
        yOffset = event.getScreenY() - getStageMouseEvent(event).getY();
    }

    // Mouse dragged handler to move the window
    public void handleMouseDragged(MouseEvent event) {
        Stage stage = getStageMouseEvent(event);

        // Move the window based on the current mouse position
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

}
