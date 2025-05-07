package com.mdev.chatcord.client;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
public class SpringBootApp {

    public static void main(String[] args) {
        Application.launch(ChatcordApplication.class, args);
    }

}
