package com.mdev.chatcord.client.annotation.aspect;

import com.mdev.chatcord.client.component.DragWindow;
import com.mdev.chatcord.client.controller.ui.EventStageHandler;
import com.mdev.chatcord.client.controller.ui.WindowController;
import jakarta.annotation.PostConstruct;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.objenesis.instantiator.gcj.GCJInstantiatorBase;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Component
@Aspect
public class WindowDragAspect extends DragWindow implements EventStageHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void setup(){
        logger.info("WindowDragAspect initialized");

    }

    @After("execution(* *.setDraggeablePressed(..)) && @within(com.mdev.chatcord.client.annotation.DraggableWindow)")
    public void makeDraggable(JoinPoint joinPoint){

        logger.info("WindowDragAspect Making {} Window Draggable!.", joinPoint.getTarget());

        try{
            Object controller = joinPoint.getTarget();

            for (Field field: controller.getClass().getDeclaredFields()){

                field.setAccessible(true);

                if (field.getName().equals("dragRegion")){
                    logger.info("JDHOFIUnfInDUIhIFUHsDOIFHoIFUhfOIUhDFIijk");
                    Object dragRegion = field.get(controller);
                    if (dragRegion instanceof HBox hBoxValue){
                        logger.info("WindowDragAspect making onDrag Logic.");

                        final Delta delta = new Delta();

                        hBoxValue.setOnMousePressed(this::handleMousePressed);
                        hBoxValue.setOnMouseDragged(this::handleMouseDragged);
                        logger.info("{} is the instance of dragRegion from + {}", dragRegion, controller);
                    }
                }
                else {
                        Object objectValue = field.get(controller);
                        logger.info((String) objectValue);
                }
            }
            Field dragRegionField = controller.getClass().getDeclaredField("dragRegion");
            //Method setOnDrag = controller.getClass().getDeclaredMethod("setOnDrag");

            //dragRegionField.setAccessible(true);




        } catch (NoSuchFieldException | IllegalAccessException exception){
            exception.printStackTrace();
        }
    }

    private static class Delta {
        double x, y;
    }
}
