package com.mdev.chatcord.client.common.service;

import com.mdev.chatcord.client.ChatcordApplication.StageReadyEvent;
import com.mdev.chatcord.client.device.dto.DeviceDto;
import com.mdev.chatcord.client.token.service.UserActivityMonitor;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class StageInitializer implements ApplicationListener<StageReadyEvent> {
    //@Value("classpath:/view/hello-view.fxml")
    //private Resource helloResource;

    @Value("classpath:/view/login/sign-view.fxml")
    private Resource signResource;

    @Getter
    private Stage primaryStage;

    @Getter
    Image APP_LOGO = new Image(getClass().getResource("/images/app_logo.png").toExternalForm());

    private final String applicationTitle;
    private final AnnotationConfigApplicationContext applicationContext;
    private final UserActivityMonitor userActivityMonitor;
    private final DeviceDto deviceDto;

    public StageInitializer(@Value("${spring.application.ui.title}") String applicationTitle, AnnotationConfigApplicationContext applicationContext, UserActivityMonitor userActivityMonitor, DeviceDto deviceDto) {
        this.applicationTitle = applicationTitle;
        this.applicationContext = applicationContext;
        this.userActivityMonitor = userActivityMonitor;
        this.deviceDto = deviceDto;
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

            primaryStage.getIcons().add(APP_LOGO);

            primaryStage.setTitle(applicationTitle);

            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.initStyle(StageStyle.TRANSPARENT); // For fully custom windows

            Scene scene = new Scene(parent, 1380, 750);
            scene.setFill(Color.TRANSPARENT); // Make scene background transparent

            scene.addEventFilter(MouseEvent.ANY,e -> userActivityMonitor.markActivity());
            scene.addEventFilter(KeyEvent.ANY, e -> userActivityMonitor.markActivity());

            primaryStage.setScene(scene);

            initializeAccess();

            primaryStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void switchScenes(Stage currentStage, String fxmlPath, String title, int width, int height) {
        try {
            Resource fxmlResource = applicationContext.getResource("classpath:" + fxmlPath);
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlResource.getURL());
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            AnchorPane parent = fxmlLoader.load();

            Rectangle clip = new Rectangle();
            clip.widthProperty().bind(parent.widthProperty());
            clip.heightProperty().bind(parent.heightProperty());
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            parent.setClip(clip);

            Scene scene = new Scene(parent, width, height);
            scene.setFill(Color.TRANSPARENT);

            Stage newStage = new Stage();
            newStage.initStyle(StageStyle.UNDECORATED);
            newStage.initStyle(StageStyle.TRANSPARENT);

            newStage.getIcons().add(APP_LOGO);

            newStage.setScene(scene);
            newStage.setTitle(title);

            // Optional: close the old stage if you want to replace it
            currentStage.close();

            primaryStage = newStage;

            primaryStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeAccess() throws IOException {

    }
}
