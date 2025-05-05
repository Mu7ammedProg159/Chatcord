package com.mdev.chatcord.client.controller.ui;

import com.mdev.chatcord.client.component.SpringFXMLLoader;
import com.mdev.chatcord.client.component.StageInitializer;
import com.mdev.chatcord.client.dto.UserDTO;
import com.mdev.chatcord.client.enums.ELoginStatus;
import com.mdev.chatcord.client.service.UserService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
public class LoginController implements UIErrorHandler {

    @FXML
    private StackPane stackPane;

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

    @Autowired
    SpringFXMLLoader springFXMLLoader;

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

    public void onSubmitClicked() throws IOException {

        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        clearStyles(emailLabel, emailField);
        clearStyles(passwordLabel, passwordField);
        clearStyles(confirmPasswordLabel, confirmPasswordField);
        clearStyles(emailLabel, usernameField);

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

                clearStyles(emailLabel, emailField);
                clearStyles(passwordLabel, passwordField);
                clearStyles(confirmPasswordLabel, confirmPasswordField);
                clearStyles(emailLabel, usernameField);

                loadOtpWindow(email);

                logger.info(registerResponse);
            } catch (RuntimeException e) {
                setError(emailLabel, "EMAIL ADDRESS – " + e.getMessage(), emailField);
                clearStyles(passwordLabel, passwordField);
                clearStyles(confirmPasswordLabel, confirmPasswordField);
                clearStyles(emailLabel, usernameField);
            } catch (IOException e) {
                throw new RuntimeException(e);
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
                emailLabel.setText("EMAIL ADDRESS – " + loginResponse);
                passwordLabel.setText("PASSWORD – " + loginResponse);

                changeLoginStatus(ELoginStatus.ERROR);

                //Change this accordingly when you have access to the Exception Handling commit.
                if (((String) loginResponse).equalsIgnoreCase(
                        "Please verify your Email Address first before logging in")){
                    loadOtpWindow(email);
                }


            } else {
                emailLabel.setText("EMAIL ADDRESS *");
                passwordLabel.setText("PASSWORD *");

                changeLoginStatus(ELoginStatus.SUCCESS);
                //statusLabel.setText("You have successfully Signed In");
                stageInitializer.switchScenes("/view/chat-view.fxml", "Chatcord", 1350, 720);
            }
        }
    }

    private void loadOtpWindow(String email) throws IOException {
        FXMLLoader otpLoader = springFXMLLoader.getLoader("/view/verification-otp.fxml");
        Parent otpOverlay = otpLoader.load();
        OtpController otpController = otpLoader.getController();

        otpController.setToEmail(email);

        stackPane.getChildren().add(otpOverlay);

        otpController.getNum0().requestFocus();

        otpController.setOnClose(() -> stackPane.getChildren().remove(otpOverlay));
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

