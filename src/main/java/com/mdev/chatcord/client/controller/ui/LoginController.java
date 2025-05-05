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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class LoginController implements UIErrorHandler {

    @FXML
    private TextField emailField, usernameField;

    @FXML
    private PasswordField passwordField, confirmPasswordField;

    @FXML
    private Label statusLabel, appNameLabel, switchLabel, titleLabel, titleSlogan, emailLabel,
            passwordLabel, confirmPasswordLabel, usernameLabel;

    @FXML
    private VBox emailVBox, usernameVBox, passwordVBox, confirmPasswordVBox;

    @FXML
    private HBox loginPanelHBox;

    @FXML
    private Button submitButton;

    @FXML
    private ImageView loginImage;

    @FXML
    private Hyperlink switchModeLink, debugLink;

    @Autowired
    UserService userService;

    @Autowired
    StageInitializer stageInitializer;

    private boolean isRegisterMode;

    private Image loginImageURL = new Image(getClass().getResource("/images/login-page-illustration.png").toExternalForm());

    private final Logger logger = LoggerFactory.getLogger(getClass());

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

    @FXML
    public void onDebugLinkClicked(){
        clearAllStyles();
        logger.info(emailLabel.getStyleClass().toString());
        logger.info(passwordLabel.getStyleClass().toString());
        logger.info(confirmPasswordLabel.getStyleClass().toString());
        logger.info(usernameLabel.getStyleClass().toString());
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

            clearAllStyles();

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
            clearAllStyles();

        }
        clearFields();
    }

    private void clearAllStyles() {
        clearStyles(emailLabel, emailField);
        clearStyles(passwordLabel, passwordField);
        clearStyles(confirmPasswordLabel, confirmPasswordField);
        clearStyles(usernameLabel, usernameField);
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

        clearAllStyles();

        if (isRegisterMode) {

            if (isAnyFieldEmpty(email, username, password, confirmPassword)) {
                setError(emailLabel, "EMAIL ADDRESS – Please fill all the fields.",
                        emailField, usernameField, passwordField, confirmPasswordField);
                return;
            }

            if (!password.equals(confirmPassword)) {
                setError(passwordLabel, "PASSWORD – Passwords do not match.", passwordField);
                setError(confirmPasswordLabel, "CONFIRM PASSWORD – Passwords do not match.", confirmPasswordField);
                return;
            }

            try {
                String registerResponse = userService.register(email, password, username);

                clearAllStyles();

                logger.info(registerResponse);
            } catch (RuntimeException e) {
                setError(emailLabel, "EMAIL ADDRESS – " + e.getMessage(), emailField);
                return;
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
                //emailLabel.setText("EMAIL ADDRESS – " + loginResponse);
                //passwordLabel.setText("PASSWORD – " + loginResponse);

                //changeLoginStatus(ELoginStatus.ERROR);
                setError(emailLabel, "EMAIL ADDRESS – " + loginResponse);
                setError(passwordLabel, "PASSWORD – " + loginResponse);

            } else {
//                emailLabel.setText("EMAIL ADDRESS *");
//                passwordLabel.setText("PASSWORD *");

                clearStyles(emailLabel, emailField);
                clearStyles(passwordLabel, passwordField);
                //changeLoginStatus(ELoginStatus.SUCCESS);
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

