package com.mdev.chatcord.client.controller.ui.login;

import com.mdev.chatcord.client.component.DragWindow;
import com.mdev.chatcord.client.implementation.EventStageHandler;
import com.mdev.chatcord.client.implementation.UIErrorHandler;
import com.mdev.chatcord.client.service.UserService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Getter
@RequiredArgsConstructor
public class OtpController extends DragWindow implements EventStageHandler, UIErrorHandler {

    @FXML private HBox dragRegion;
    @FXML private TextFlow textFlow, successTextFlow;
    @FXML private HBox backgroundHBox;
    @FXML private Label toEmail, headerLabel, subHeaderLabel, resendLabel;
    @FXML private TextField num0, num1, num2, num3, num4, num5;
    @FXML private Hyperlink resendLink;
    @FXML private VBox otpVBox;
    @FXML private ImageView otpImage;
    @FXML private Button verifyButton;

    private String otp;

    private boolean isVerifiedMode = false;

    private Timeline countdownTimer;
    private long secondsRemaining;

    @Setter
    private Runnable onClose;

    private boolean isReadyToRetry = false;

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @FXML
    public void initialize(){

        dragRegion.setOnMousePressed(this::handleMousePressed);
        dragRegion.setOnMouseDragged(this::handleMouseDragged);

        successTextFlow.setVisible(false);
        successTextFlow.setManaged(false);
        initializeFieldsProperties();
        isReadyToRetry = true;
    }

    public void setToEmail(String email){
        toEmail.setText(email);
        startCountdown();
        try {
            userService.resendOtp(toEmail.getText());
        } catch (RuntimeException e){
            logger.error(e.getMessage());
        }
        startCountdown();
    }

    @FXML
    public void onVerifyClicked(){
        if (isVerifiedMode)
            onClose.run();
        else
            onVerify();
    }

    public void onVerify(){
        String email = toEmail.getText();
        otp = num0.getText() + num1.getText() + num2.getText() + num3.getText() + num4.getText() + num5.getText();
        try{
            userService.verify(email, otp);
            logger.info("Now I should do /verify-email Webclient"); // move to next field
            switchToVerified();
        } catch (RuntimeException e) {
            setError(null, null, num0, num1, num2, num3, num4, num5);
            logger.info(e.getMessage());
        }
    }

    @FXML
    public void onResendLinkClicked(){
        num0.requestFocus();
        try {
            userService.resendOtp(toEmail.getText());
        } catch (RuntimeException e){
            logger.error(e.getMessage());
        }
        startCountdown();
    }

    @FXML
    public void cancelOtp(MouseEvent event){
        if (event.getTarget() == backgroundHBox && onClose != null) {
            onClose.run();
        }
    }

    private void startCountdown() {
        try{
            secondsRemaining = userService.canResendOtp(toEmail.getText());

        } catch (RuntimeException e){
            logger.error(e.getMessage());
        }

        if (secondsRemaining > 0){
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
            countdownTimer.setCycleCount((int) secondsRemaining); // Run it 60 times
            countdownTimer.play();
        }
    }

    public void switchToVerified(){
        // I need to disable & set managed to false for: 1- otpVBox, 2- resendLink Hyperlink, resendLabel, toEmail label, maybe the Textflow.
        setVisibility(false, otpVBox, textFlow, toEmail, subHeaderLabel);

        resendLabel.setText("Still need a help ?");
        resendLink.setText("help");

        // Change headerLabel to be a successful message
        headerLabel.setText("Email Verified Successfully!");

        setVisibility(true, successTextFlow);

        // Change the image illustration to show a success procedure.
        Image successIimage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/verification_success_illustration.png")));
        otpImage.setImage(successIimage);

        // Change verifyButton's text into Log In
        verifyButton.setText("Login");

        // add any further details if needed
        isVerifiedMode = true;
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

        handleFieldsOnKeyPressed(current, previous, otpFields);
    }

    private void handleFieldsOnKeyPressed(TextField current, TextField previous, List<TextField> otpFields) {
        // Handle backspace key to move to previous field
        current.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE && current.getText().isEmpty() && previous != null) {
                previous.requestFocus();
            } else { // if you want to handle any other keys, just add else if with same procedure.
                for (TextField tf : otpFields) {
                    if ((event.isControlDown() || event.isMetaDown()) && event.getCode() == KeyCode.V) {
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
                }
            }
        });
    }
}
