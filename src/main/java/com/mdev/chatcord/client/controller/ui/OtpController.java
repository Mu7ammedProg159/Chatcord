package com.mdev.chatcord.client.controller.ui;

import com.mdev.chatcord.client.service.UserService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class OtpController implements EventStageHandler, UIErrorHandler{

    @FXML private HBox backgroundHBox;
    @FXML private Label toEmail;
    @FXML private TextField num0, num1, num2, num3;
    @FXML private Hyperlink resendLink;

    private String otp;

    private Timeline countdownTimer;
    private int secondsRemaining = 60;

    @Setter
    private Runnable onClose;

    private boolean isReadyToRetry = false;

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void setToEmail(String email){
        toEmail.setText(email);
    }

    @FXML
    public void initialize(){
        initializeFieldsProperties();
        isReadyToRetry = true;
    }

    @FXML
    public void onVerifyClicked(){
        onVerify();
    }

    public void onVerify(){
        otp = num0.getText() + num1.getText() + num2.getText() + num3.getText();
        String email = toEmail.getText();
        try{
            userService.verify(email, otp);

        } catch (RuntimeException e) {
            setError(null, null, num0);
            setError(null, null, num1);
            setError(null, null, num2);
            setError(null, null, num3);
        }

    }

    @FXML
    public void onResendLinkClicked(){
        num0.requestFocus();
        userService.resendOtp(toEmail.getText());
        startCountdown();
    }

    @FXML
    public void cancelOtp(MouseEvent event){
        if (event.getTarget() == backgroundHBox && onClose != null) {
            onClose.run();
        }
    }

    private void startCountdown() {
        resendLink.setDisable(true); // Disable the resend button

        countdownTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsRemaining--;
            resendLink.setText("Resend in: " + secondsRemaining + "s");

            if (secondsRemaining <= 0) {
                countdownTimer.stop();
                resendLink.setText("Resend"); // or "You can resend now"
                resendLink.setDisable(false);
                secondsRemaining = 60; // Reset if you want to use again
            }
             else {
                 resendLink.setDisable(true);
            }
        }));
        countdownTimer.setCycleCount(secondsRemaining); // Run it 60 times
        countdownTimer.play();
    }


    // From here the logic is for automatic fill for OTP Code.
    private void initializeFieldsProperties(){
        onOtpFilled(num0, num1, null);

        onOtpFilled(num1, num2, num0);

        onOtpFilled(num2, num3, num1);

        final boolean[] isAllFilled = {false};

        num3.textProperty().addListener((obs, oldText, newText) -> {

            if (!newText.matches("\\d*")) {
                num3.setText(newText.replaceAll("[^\\d]", ""));
            }

            if (num3.getText().length() > 1) {
                num3.setText(num3.getText().substring(0, 1));
            }

            if (!isAllFilled[0] && num3.getText().length() == 1) {
                isAllFilled[0] = true;
                logger.info("Now I should do /verify-email Webclient"); // move to next field
            }
        });
        num3.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE && num3.getText().isEmpty() && num2 != null) {
                isAllFilled[0] = false;
                num2.requestFocus();
            }
        });
    }

    private void onOtpFilled(TextField current, TextField next, TextField previous) {
        current.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                current.setText(newText.replaceAll("[^\\d]", ""));
            }

            if (current.getText().length() > 1) {
                current.setText(current.getText().substring(0, 1));
            }

            if (current.getText().length() == 1) {
                next.requestFocus(); // move to next field
            }
        });

        // Handle backspace key to move to previous field
        current.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE && current.getText().isEmpty() && previous != null) {
                previous.requestFocus();
            }
        });

    }
}
