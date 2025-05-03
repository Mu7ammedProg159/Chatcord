package com.mdev.chatcord.client.controller.ui;

import com.mdev.chatcord.client.component.StageInitializer;
import com.mdev.chatcord.client.dto.UserDTO;
import com.mdev.chatcord.client.enums.ELoginStatus;
import com.mdev.chatcord.client.service.UserService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class LoginController {

    @FXML
    private TextField emailField, usernameField;

    @FXML
    private PasswordField passwordField, confirmPasswordField;

    @FXML
    private Label statusLabel, appNameLabel, switchLabel, titleLabel, titleSlogan, emailLabel,
            passwordLabel, confirmPasswordLabel;

    @FXML
    private VBox emailVBox, usernameVBox, passwordVBox, confirmPasswordVBox;

    @FXML
    private HBox loginPanelHBox;

    @FXML
    private Button submitButton;

    @FXML
    private ImageView loginImage;

    @FXML
    private Hyperlink switchModeLink;

    @Autowired
    UserService userService;

    @Autowired
    StageInitializer stageInitializer;

    private boolean isRegisterMode;

    private Image loginImageURL = new Image(getClass().getResource("/images/login-page-illustration.png").toExternalForm());

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
        switchModeLink.setVisited(false);
        updateMode();
    }

    public void updateMode() {
        if (isRegisterMode) {
            titleLabel.setText("Create Account");
            submitButton.setText("Register");
            switchLabel.setText("Already have an account?");
            switchModeLink.setText("Log In");
            titleSlogan.setText("Don’t stay alone – get started!");

            loginImageURL = new Image(getClass().getResource("/images/register_background.png").toExternalForm());


            hideFields(true);
            loginImage.setImage(loginImageURL);

            // Just reverse the children order
            //Collections.swap(loginPanelChildren, 0, 1);

        } else {
            titleLabel.setText("Welcome back!");
            submitButton.setText("Log In");
            switchLabel.setText("Don't you have an account?");
            switchModeLink.setText("Register");
            titleSlogan.setText("We're so excited to see you again!");

            hideFields(false);

            loginImageURL = new Image(getClass().getResource("/images/login-page-illustration.png").toExternalForm());
            loginImage.setImage(loginImageURL);
            //Collections.swap(loginPanelChildren, 1, 0);

        }
        clearFields();
    }

    private void hideFields(boolean b) {
        usernameField.setVisible(b);
        usernameField.setManaged(b);
        confirmPasswordField.setVisible(b);
        confirmPasswordField.setManaged(b);
        usernameVBox.setVisible(b);
        usernameVBox.setManaged(b);
        confirmPasswordVBox.setVisible(b);
        confirmPasswordVBox.setManaged(b);
    }

    public void onSubmitClicked() {

        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        changeLoginStatus(ELoginStatus.SUCCESS);

        if (isRegisterMode) {

            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                emailLabel.setText("EMAIL ADDRESS – " + "Please fill all the fields.");
                emailLabel.getStyleClass().add("credentialLabelError");
                emailField.getStyleClass().add("textFieldError");
                usernameField.getStyleClass().add("textFieldError");
                passwordField.getStyleClass().add("textFieldError");
                confirmPasswordField.getStyleClass().add("textFieldError");
                return;
            }
            if (!password.equals(confirmPassword)) {
                passwordLabel.setText("PASSWORD – " + "Passwords do not match.");
                confirmPasswordLabel.setText("CONFIRM PASSWORD – Passwords do not match.");
                passwordLabel.getStyleClass().add("credentialLabelError");
                confirmPasswordLabel.getStyleClass().add("credentialLabelError");
                passwordField.getStyleClass().add("textFieldError");
                confirmPasswordField.getStyleClass().add("textFieldError");
                return;
            }
            //Todo: Call user registration service for database management.
//            if (!authService.isUserRegistered(email)){
//                authService.signUp(email, username, password);
//                statusLabel.setText("Account Created!");
//                isRegisterMode = !isRegisterMode;
            try{
                String registerResponse = userService.register(email, password, username);

                emailLabel.setText("EMAIL ADDRESS *");
                passwordLabel.setText("PASSWORD *");

                emailLabel.getStyleClass().add("credentialLabel");
                emailField.getStyleClass().add("textField");

                usernameField.getStyleClass().add("textField");

                passwordField.getStyleClass().add("textField");
                confirmPasswordField.getStyleClass().add("textField");
            } catch (RuntimeException e){
                emailLabel.setText("EMAIL ADDRESS – " + e.getMessage());
                emailLabel.getStyleClass().add("credentialLabelError");
            }

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

            var loginResponse = userService.login(email, password);

            if (loginResponse instanceof String) {
                statusLabel.setText((String) loginResponse);
                emailLabel.setText("EMAIL ADDRESS – " + loginResponse);
                passwordLabel.setText("PASSWORD – " + loginResponse);

                changeLoginStatus(ELoginStatus.ERROR);

            } else {
                emailLabel.setText("EMAIL ADDRESS *");
                passwordLabel.setText("PASSWORD *");

                changeLoginStatus(ELoginStatus.SUCCESS);
                //statusLabel.setText("You have successfully Signed In");
                stageInitializer.switchScenes("/view/chat-view.fxml", "Chatcord", 1350, 720);
            }
        }
    }

    private void changeLoginStatus(ELoginStatus eLoginStatus) {

        if (eLoginStatus.equals(ELoginStatus.SUCCESS)) {
            emailLabel.getStyleClass().add("credentialLabel");
            passwordLabel.getStyleClass().add("credentialLabel");
            emailField.getStyleClass().add("textField");
            passwordField.getStyleClass().add("textField");
        }
        else if (eLoginStatus.equals(ELoginStatus.ERROR)) {
            emailLabel.getStyleClass().add("credentialLabelError");
            passwordLabel.getStyleClass().add("credentialLabelError");
            emailField.getStyleClass().add("textFieldError");
            passwordField.getStyleClass().add("textFieldError");
        }
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        statusLabel.setText("");
    }
}

