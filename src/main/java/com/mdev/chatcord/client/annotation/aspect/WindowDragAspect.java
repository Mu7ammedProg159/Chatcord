package com.mdev.chatcord.client.annotation.aspect;

import com.mdev.chatcord.client.controller.ui.EventStageHandler;
import jakarta.annotation.PostConstruct;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Aspect
@Component
public class WindowDragAspect implements EventStageHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private HBox dragRegion;

    @PostConstruct
    public void setup(){
        logger.info("WindowDragAspect initialized");

    }

    @After("com.mdev.chatcord.client.annotation.DraggableWindow")
    public void makeDraggable(JoinPoint joinPoint){
        Object controller = joinPoint.getTarget();
        try{
            Field dragRegionField = controller.getClass().getDeclaredField("dragRegion");
            dragRegionField.setAccessible(true);
            dragRegion = (HBox) dragRegionField.get(controller);

            final Delta delta = new Delta();

            dragRegion.setOnMousePressed( mouseEvent -> {
                Stage stage = getStageMouseEvent(mouseEvent);
                delta.x = stage.getX() - mouseEvent.getScreenX();
                delta.y = stage.getY() - mouseEvent.getScreenY();
            });

            dragRegion.setOnMouseDragged(mouseEvent -> {
                Stage stage = getStageMouseEvent(mouseEvent);
                stage.setX(mouseEvent.getScreenX() - delta.x);
                stage.setY(mouseEvent.getScreenY() - delta.y);
            });

        } catch (NoSuchFieldException | IllegalAccessException exception){
            exception.printStackTrace();
        }
    }

    private static class Delta {
        double x, y;
    }
}
