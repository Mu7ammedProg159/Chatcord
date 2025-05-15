package com.mdev.chatcord.client.controller.ui.login;

import com.mdev.chatcord.client.component.SpringFXMLLoader;
import com.mdev.chatcord.client.component.StageInitializer;
import com.mdev.chatcord.client.controller.ui.essential.LoadingController;
import com.mdev.chatcord.client.implementation.LoadingHandler;
import com.mdev.chatcord.client.implementation.ThrowingRunnable;
import com.mdev.chatcord.client.enums.ELoginStatus;
import com.mdev.chatcord.client.implementation.UIHandler;
import com.mdev.chatcord.client.service.UserService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class LoginController implements LoadingHandler, UIHandler {

    @FXML private StackPane stackPane;

    @FXML private TextField emailField, usernameField;

    @FXML private PasswordField passwordField, confirmPasswordField;

    @FXML private Label statusLabel, appNameLabel, switchLabel, titleLabel, titleSlogan, emailLabel, passwordLabel, confirmPasswordLabel, usernameLabel;

    @FXML private VBox emailVBox, usernameVBox, passwordVBox, confirmPasswordVBox;

    @FXML private HBox loginPanelHBox, loadingComponent;

    @FXML private Button submitButton;

    @FXML private ImageView loginImage;

    @FXML private Hyperlink switchModeLink;

    @Autowired
    UserService userService;

    @Autowired
    StageInitializer stageInitializer;

    @Autowired
    LoadingController loadingController;

    @Autowired
    SpringFXMLLoader springFXMLLoader;

    private Stage stage;

    private boolean isRegisterMode;

    private final String [] labelString = {"EMAIL ADDRESS *", "PASSWORD *", "CONFIRM PASSWORD *", "USERNAME *"};

    String email = null;
    String password = null;
    String confirmPassword = null;
    String username = null;

    Map<Label, TextField> formData;


    private Image loginImageURL = new Image(getClass().getResource("/images/login-page-illustration.png").toExternalForm());

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @FXML
    public void initialize() {
        changeFont(appNameLabel, "/fonts/CarterOne-Regular.ttf", 23);
        formData = new LinkedHashMap<>();
        formData.put(emailLabel, emailField);
        formData.put(passwordLabel, passwordField);
        formData.put(confirmPasswordLabel, confirmPasswordField);
        formData.put(usernameLabel, usernameField);
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


            hideFields(true, usernameField, confirmPasswordField, usernameVBox, confirmPasswordVBox);
            loginImage.setImage(loginImageURL);

            // Just reverse the children order
            //Collections.swap(loginPanelChildren, 0, 1);

        } else {
            titleLabel.setText("Welcome back!");
            submitButton.setText("Log In");
            switchLabel.setText("Don't you have an account?");
            switchModeLink.setText("Register");
            titleSlogan.setText("We're so excited to see you again!");

            hideFields(false, usernameField, confirmPasswordField, usernameVBox, confirmPasswordVBox);

            loginImageURL = new Image(getClass().getResource("/images/login-page-illustration.png").toExternalForm());
            loginImage.setImage(loginImageURL);
            //Collections.swap(loginPanelChildren, 1, 0);

        }
        clearAllStylesWithDefaultText();
        clearFields();
    }

    private void hideFields(boolean b, Node... nodes) {
        for (Node node: nodes) {
            node.setVisible(b);
            node.setManaged(b);
        }
    }

    public void onSubmitClicked() throws IOException {

        stage = (Stage) submitButton.getScene().getWindow();

        email = emailField.getText();
        username = usernameField.getText();
        password = passwordField.getText();
        confirmPassword = confirmPasswordField.getText();

        if (isRegisterMode) {

            if (isAnyFieldEmpty(email, username, password, confirmPassword)) {
                setError(emailLabel, "EMAIL ADDRESS – Please fill all the fields.", emailField, usernameField, passwordField, confirmPasswordField);
                return;
            }

            if (!password.equals(confirmPassword)) {
                setError(passwordLabel, "PASSWORD – Passwords do not match.", passwordField);
                setError(confirmPasswordLabel, "CONFIRM PASSWORD – Passwords do not match.", confirmPasswordField);
                return;
            }

            try {
                String registerResponse = userService.register(email, password, username);

                loadOtpWindow(email);

                logger.info(registerResponse);
            } catch (RuntimeException e) {
                setError(emailLabel, "EMAIL ADDRESS – " + e.getMessage(), emailField);
                clearStyles(passwordLabel, passwordField);
                clearStyles(confirmPasswordLabel, confirmPasswordField);
                clearStyles(usernameLabel, usernameField);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            updateMode();

        } else {
            if (email.isEmpty()) {
                setError(emailLabel, labelString[0] + " – Please enter your email.");
                return;
            }
            if (password.isEmpty()) {
                setError(passwordLabel, "Please enter your password.");
                return;
            }

            loadingController.onLoad(loadOnCall(), loadOnSuccess(), loadOnFailure());

            changeLoginStatus(ELoginStatus.SUCCESS);
        }
    }

    private void loadOtpWindow(String email) throws IOException {
        FXMLLoader otpLoader = springFXMLLoader.getLoader("/view/verification-otp.fxml");
        Parent otpOverlay = otpLoader.load();
        OtpController otpController = otpLoader.getController();

        otpController.setToEmail(email);

        stackPane.getChildren().add(otpOverlay);

        otpController.getNum0().requestFocus();

        otpController.setOnClose(() -> {
            stackPane.getChildren().remove(otpOverlay);
            clearAllStylesWithDefaultText();
            isRegisterMode = false;
            submitButton.requestFocus();
            updateMode();
        });
    }

    private void clearAllStylesWithDefaultText() {

        int index = 0;

        for (Map.Entry<Label, TextField> entry : formData.entrySet()){
            entry.getKey().setText(labelString[index]);
            clearStyles(entry.getKey(), entry.getValue());
            index++;
        }
    }

    private void changeLoginStatus(ELoginStatus eLoginStatus) {

        if (eLoginStatus.equals(ELoginStatus.SUCCESS)) {
            for (Map.Entry<Label, TextField> entry: formData.entrySet()) {
                entry.getKey().getStyleClass().add("credentialLabel");
                entry.getValue().getStyleClass().add("textField");
            }
        }
        else if (eLoginStatus.equals(ELoginStatus.ERROR)) {
            for (Map.Entry<Label, TextField> entry: formData.entrySet()) {
                entry.getKey().getStyleClass().add("credentialLabelError");
                entry.getValue().getStyleClass().add("textFieldError");
            }
        }
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    @Override
    public ThrowingRunnable loadOnCall() {
        return () -> {
            Platform.runLater(() -> submitButton.setText(""));
            try {
                userService.login(email, password);
            } catch (RuntimeException e) {
                Platform.runLater(() -> {
                    emailLabel.setText(labelString[0] + " – " + e.getMessage());
                    passwordLabel.setText(labelString[1] + " – " + e.getMessage());
                    submitButton.setText("Login");
                    changeLoginStatus(ELoginStatus.ERROR);

                    if (e.getMessage().equalsIgnoreCase("Please verify your email address to login.")) {
                        try {
                            loadOtpWindow(email);
                        } catch (IOException ex) {
                            logger.error(ex.getMessage());
                        }
                    }
                });
            }
        };
    }

    @Override
    public ThrowingRunnable loadOnSuccess() {
        return () -> {
            Platform.runLater(() -> submitButton.setText("Log In"));
            try {
                stageInitializer.switchScenes(stage, "/view/main-layout/main-view.fxml", "Chatcord", 1350, 720);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public ThrowingRunnable loadOnFailure() {
        return () -> {
            Platform.runLater(() -> submitButton.setText("Login"));
        };
    }
}

