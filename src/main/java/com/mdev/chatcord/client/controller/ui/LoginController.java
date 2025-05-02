package com.mdev.chatcord.client.controller.ui;

import com.mdev.chatcord.client.component.StageInitializer;
import com.mdev.chatcord.client.dto.UserDTO;
import com.mdev.chatcord.client.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button submitButton;

    @FXML
    private Label titleLabel;

    @FXML
    private Label switchLabel;

    @FXML
    private Hyperlink switchModeLink;

    @FXML
    private Label statusLabel;

    @FXML
    private Label appNameLabel;

    private boolean isRegisterMode;

    @Autowired
    UserService userService;

    @Autowired
    StageInitializer stageInitializer;

    @FXML
    public void initialize() {
        appNameLabel.setFont(Font.loadFont(getClass().
                getResourceAsStream("/fonts/CarterOne-Regular.ttf"),
                23));
        updateMode();
    }

    @FXML
    public void onSwitchModeClicked() {
        isRegisterMode = !isRegisterMode;
        updateMode();
    }

    public void updateMode() {
        if (isRegisterMode) {
            titleLabel.setText("Create Account");
            submitButton.setText("Register");
            switchLabel.setText("Already have an account?");
            switchModeLink.setText("Sign In");
            usernameField.setVisible(true);
            usernameField.setManaged(true);
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
        } else {
            titleLabel.setText("Login to Your Account");
            submitButton.setText("Sign In");
            switchLabel.setText("Don't you have an account?");
            switchModeLink.setText("Register");
            usernameField.setVisible(false);
            usernameField.setManaged(false);
            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
        }
        clearFields();
    }

    public void onSubmitClicked() {

        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (isRegisterMode) {
            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                statusLabel.setText("Please fill all the fields.");
                return;
            }
            if (!password.equals(confirmPassword)) {
                statusLabel.setText("Passwords do not match.");
                return;
            }
            //Todo: Call user registration service for database management.
//            if (!authService.isUserRegistered(email)){
//                authService.signUp(email, username, password);
//                statusLabel.setText("Account Created!");
//                isRegisterMode = !isRegisterMode;
            updateMode();
            //}
//            else {
//                statusLabel.setText("Account already registered with this Email Address.");
//            }
        } else {
            if (email.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please enter your email or password.");
                return;
            }
            // TODO: Call user login service from database.

            UserDTO userDTO = userService.login(email, password);

            if (userDTO == null) {
                statusLabel.setText("Email or Password are incorrect.");
            } else {
                statusLabel.setText("You have successfully Signed In");
                stageInitializer.switchScenes("/view/chat-view.fxml", "Chatcord", 1350, 720);
            }
        }
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        statusLabel.setText("");
    }
}

