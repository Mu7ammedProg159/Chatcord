package com.mdev.messanger.client.component;

import com.mdev.messanger.client.MessangerApplication.StageReadyEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {
    //@Value("classpath:/view/hello-view.fxml")
    //private Resource helloResource;

    @Value("classpath:/view/sign-view.fxml")
    private Resource signResource;

    private Stage primaryStage;

    private final String applicationTitle;
    private ApplicationContext applicationContext;

    public StageInitializer(@Value("${spring.application.ui.title}") String applicationTitle, ApplicationContext applicationContext) {
        this.applicationTitle = applicationTitle;
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {


        // to-do Here you can add your details for JavaFx
            this.primaryStage = event.getStage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(signResource.getURL());
            fxmlLoader.setControllerFactory(aClass -> applicationContext.getBean(aClass));
            Parent parent = fxmlLoader.load();

            primaryStage.setScene(new Scene(parent, 800, 600));
            primaryStage.setTitle(applicationTitle);
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
