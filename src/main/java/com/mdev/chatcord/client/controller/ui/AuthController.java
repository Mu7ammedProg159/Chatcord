package com.mdev.chatcord.client.controller.ui;

import com.mdev.chatcord.client.component.StageInitializer;
import com.mdev.chatcord.client.service.AuthService;
import com.mdev.chatcord.client.service.TokenHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthController {

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

    private boolean isRegisterMode;

    @Autowired
    AuthService authService;

    @Autowired
    StageInitializer stageInitializer;

    @Autowired
    TokenHandler tokenHandler;

    @FXML
    public void initialize(){
        updateMode();
    }

    @FXML
    public void onSwitchModeClicked(){
        isRegisterMode = !isRegisterMode;
        updateMode();
    }

    public void updateMode(){
        if (isRegisterMode){
            titleLabel.setText("Create Account");
            submitButton.setText("Register");
            switchLabel.setText("Already have an account?");
            switchModeLink.setText("Sign In");
            usernameField.setVisible(true);
            usernameField.setManaged(true);
            confirmPasswordField.setVisible(true);
            confirmPasswordField.setManaged(true);
        }
        else {
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

    public void onSubmitClicked(){

        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (isRegisterMode){
            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
                statusLabel.setText("Please fill all the fields.");
                return;
            }
            if (!password.equals(confirmPassword)){
                statusLabel.setText("Passwords do not match.");
                return;
            }
            //Todo: Call user registration service for database management.
            if (!authService.isUserRegistered(email)){
                authService.signUp(email, username, password);
                statusLabel.setText("Account Created!");
                isRegisterMode = !isRegisterMode;
                updateMode();
            }
            else {
                statusLabel.setText("Account already registered with this Email Address.");
            }
        }
        else {
            if (email.isEmpty() || password.isEmpty()){
                statusLabel.setText("Please enter your email or password.");
                return;
            }
            // TODO: Call user login service from database.
            String userToken = authService.signIn(email, password);
            if (userToken != null) {
                statusLabel.setText("Signed In with token: " + userToken);
                tokenHandler.setToken(userToken);
                stageInitializer.switchScenes("/view/chat-view.fxml", "Chatcord", 800, 600);
            }
            else
                statusLabel.setText("Username or password are incorrect.");
        }
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        statusLabel.setText("");
    }
}