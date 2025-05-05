package com.mdev.chatcord.client.controller.ui;

import com.mdev.chatcord.client.service.UserService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@RequiredArgsConstructor
public class OtpController implements EventStageHandler, UIErrorHandler{

    @FXML private HBox backgroundHBox;
    @FXML private Label toEmail;
    @FXML private TextField num0, num1, num2, num3, num4, num5;
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
        String email = toEmail.getText();
        otp = num0.getText() + num1.getText() + num2.getText() + num3.getText() + num4.getText() + num5.getText();
        try{
            userService.verify(email, otp);
            onClose.run();
        } catch (RuntimeException e) {
            setError(null, null, num0);
            setError(null, null, num1);
            setError(null, null, num2);
            setError(null, null, num3);
            setError(null, null, num4);
            setError(null, null, num5);
            logger.info(e.getMessage());
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

        onOtpFilled(num3, num4, num2);

        onOtpFilled(num4, num5, num3);

        final boolean[] isAllFilled = {false};

        num5.textProperty().addListener((obs, oldText, newText) -> {

            if (!newText.matches("\\d*")) {
                num5.setText(newText.replaceAll("[^\\d]", ""));
            }

            if (num5.getText().length() > 1) {
                num5.setText(num5.getText().substring(0, 1));
            }

            if (!isAllFilled[0] && num5.getText().length() == 1) {
                isAllFilled[0] = true;

                try{
                    onVerify();

                } catch (RuntimeException e){
                    setError(null, null, num0);
                    setError(null, null, num1);
                    setError(null, null, num2);
                    setError(null, null, num3);
                    setError(null, null, num4);
                    setError(null, null, num5);
                    return;
                }
                logger.info("Now I should do /verify-email Webclient"); // move to next field
            }
        });
        num5.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE && num5.getText().isEmpty() && num4 != null) {
                isAllFilled[0] = false;
                num4.requestFocus();
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

        List<TextField> otpFields = List.of(num0, num1, num2, num3, num4, num5);

        for (TextField tf : otpFields) {
            tf.setOnKeyPressed(e -> {
                // Detect Ctrl+V or Cmd+V
                if ((e.isControlDown() || e.isMetaDown()) && e.getCode() == KeyCode.V) {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    if (clipboard.hasString()) {
                        String pasted = clipboard.getString().replaceAll("[^\\d]", "");
                        if (pasted.length() == otpFields.size()) {
                            for (int j = 0; j < otpFields.size(); j++) {
                                otpFields.get(j).setText(String.valueOf(pasted.charAt(j)));
                            }
                            otpFields.get(otpFields.size() - 1).requestFocus();
                        }
                    }
                }
            });
        }

        // Handle backspace key to move to previous field
        current.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE && current.getText().isEmpty() && previous != null) {
                previous.requestFocus();
            }
        });

    }
}
