package com.mdev.chatcord.client.settings.controller;

import com.mdev.chatcord.client.authentication.dto.HttpRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Component
public class AccountSettingsController {


    @FXML
    private Label emailLabel;

    @FXML
    private Hyperlink changePfp;

    @FXML
    private ImageView profileImage;

    @FXML
    private Label tagLabel;

    @FXML
    private Label usernameLabel;

    @Autowired
    private HttpRequest jwtRequest;

    @FXML
    public void initialize(){
        emailLabel.setText(jwtRequest.getUserDTO().getEmail());
        usernameLabel.setText(jwtRequest.getUserDTO().getUsername());
        tagLabel.setText(jwtRequest.getUserDTO().getTag());
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
                profileImage.setImage(image);

                // Optionally save it somewhere inside the project for now
                Path destination = Paths.get("data", "images", "SERVER_" + selectedFile.getName());
                Files.createDirectories(destination.getParent()); // ensure dir exists
                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                changePfp.setVisible(true);
                System.out.println("Image copied to: " + destination.toAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
