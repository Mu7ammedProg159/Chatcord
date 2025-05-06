package com.mdev.chatcord.client.component;

import com.mdev.chatcord.client.ChatcordApplication.StageReadyEvent;
import com.mdev.chatcord.client.annotation.aspect.DraggableWindowInitializer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {
    //@Value("classpath:/view/hello-view.fxml")
    //private Resource helloResource;

    @Value("classpath:/view/login/sign-view.fxml")
    private Resource signResource;

    @Getter
    private Stage primaryStage;

    private final String applicationTitle;
    private final AnnotationConfigApplicationContext applicationContext;

    public StageInitializer(@Value("${spring.application.ui.title}") String applicationTitle, AnnotationConfigApplicationContext applicationContext) {
        this.applicationTitle = applicationTitle;
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {

        // to-do Here you can add your details for JavaFx
            this.primaryStage = event.getStage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(signResource.getURL());
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            AnchorPane parent = fxmlLoader.load();

            Rectangle clip = new Rectangle();
            clip.widthProperty().bind(parent.widthProperty());
            clip.heightProperty().bind(parent.heightProperty());
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            parent.setClip(clip);

            Image APP_LOGO = new Image(getClass().getResource("/images/app_logo.png").toExternalForm());

            primaryStage.getIcons().add(APP_LOGO);

            primaryStage.setTitle(applicationTitle);

            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.initStyle(StageStyle.TRANSPARENT); // For fully custom windows

            Scene scene = new Scene(parent, 1380, 750);
            scene.setFill(Color.TRANSPARENT); // Make scene background transparent

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public void switchScenes(String fxmlPath, String title, int width, int height) {
        try {
            Resource fxmlResource = applicationContext.getResource("classpath:" + fxmlPath);
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlResource.getURL());
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlLoader.load();

            primaryStage.setScene(new Scene(parent, width, height));
            primaryStage.setTitle(title);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load: " + fxmlPath, e);
        }

    }
}
