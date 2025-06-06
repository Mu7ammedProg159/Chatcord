package com.mdev.chatcord.client.settings.controller;

import com.mdev.chatcord.client.authentication.dto.HttpRequest;
import com.mdev.chatcord.client.common.implementation.UIHandler;
import com.mdev.chatcord.client.user.dto.ProfileDetails;
import com.mdev.chatcord.client.user.service.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import org.onyxfx.graphics.controls.OFxAvatarView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Component
@Slf4j
public class AccountSettingsController implements UIHandler {


    @FXML
    private Label emailLabel;

    @FXML
    private Hyperlink changePfp;

    @FXML
    private OFxAvatarView profileImage;

    @FXML
    private Label tagLabel;

    @FXML
    private Label usernameLabel;

    @Autowired
    private User userDetails;

    @FXML
    public void initialize(){
        profileImage.setBackgroundColor(Color.web(userDetails.getAvatarColor()));
        if (userDetails.getPfpUrl() != null){
            profileImage.setUploadedImage(createImage(userDetails.getPfpUrl()));

        }

        emailLabel.setText(userDetails.getEmail());
        usernameLabel.setText(userDetails.getUsername());
        tagLabel.setText(userDetails.getTag());
    }

    @FXML
    public void onChangePhoto(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();

        // Set extension filter for image files
        FileChooser.ExtensionFilter imageFilter =
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);

        // Optional: Set title and initial directory
        fileChooser.setTitle("Choose Profile Picture");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Open file dialog
        File selectedFile = fileChooser.showOpenDialog(changePfp.getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Load image
                Image image = new Image(selectedFile.toURI().toString());

                // Set it to the ImageView
                profileImage.setUploadedImage(image);
                profileImage.setHoverColor(Color.web("#1e1e1e"));
                profileImage.setPressedColor(Color.web("#141414"));

                // Optionally save it somewhere inside the project for now
                Path destination = Paths.get("data", "images", "SERVER_" + selectedFile.getName());
                Files.createDirectories(destination.getParent()); // ensure dir exists
                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                changePfp.setVisible(true);
                log.info("Image copied to: {}", destination.toAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
