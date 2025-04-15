package com.mdev.messanger.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

@Component
public class HelloFxController {
    @FXML
    private Label welcomeText;

    @FXML
    private TextField username;

    @FXML
    private TextField key;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void registerSubmit(javafx.event.ActionEvent e){
        System.out.println("This is your username and Password: "
                + username.getText() + ", " + key.getText());
    }
}
