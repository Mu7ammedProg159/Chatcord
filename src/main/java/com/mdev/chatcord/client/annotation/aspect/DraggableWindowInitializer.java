package com.mdev.chatcord.client.annotation.aspect;

import com.mdev.chatcord.client.annotation.DraggableWindow;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

//@Component
public class DraggableWindowInitializer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void initializeDraggable(Object controller) {
        if (controller.getClass().isAnnotationPresent(DraggableWindow.class)) {
            try {
                Field dragRegionField = controller.getClass().getDeclaredField("dragRegion");
                dragRegionField.setAccessible(true);
                Node node = (Node) dragRegionField.get(controller);
                logger.info("The Node: " + node);
                if (node == null) {
                    System.err.println("dragRegion is null even after FXML load.");
                    return;
                }

                final Delta delta = new Delta();

                node.setOnMousePressed(mouseEvent -> {
                    Stage stage = (Stage) node.getScene().getWindow();
                    logger.info("Pressed!");
                    delta.x = stage.getX() - mouseEvent.getScreenX();
                    delta.y = stage.getY() - mouseEvent.getScreenY();
                });

                node.setOnMouseDragged(mouseEvent -> {
                    Stage stage = (Stage) node.getScene().getWindow();
                    logger.info("Dragged!");
                    stage.setX(mouseEvent.getScreenX() + delta.x);
                    stage.setY(mouseEvent.getScreenY() + delta.y);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class Delta {
        double x, y;
    }
}