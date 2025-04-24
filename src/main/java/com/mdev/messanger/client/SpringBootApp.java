package com.mdev.messanger.client;

import javafx.application.Application;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringBootApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(SpringBootApp.class)
                .web(WebApplicationType.SERVLET) // ensures Tomcat starts
                .run(args);

        // Now launch JavaFX app with Spring context
        MessangerApplication.setApplicationContext(context);
        Application.launch(MessangerApplication.class, args);
    }

}
