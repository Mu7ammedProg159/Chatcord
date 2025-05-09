package com.mdev.chatcord.client.controller.ui.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@AllArgsConstructor
public class FriendsController {

    @FXML private Button addContactButton;
    @FXML TextField searchField;
    @FXML VBox contactsListView;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @FXML
    public void initialize(){

    }

    public void onAddContactClick(MouseEvent mouseEvent) {

    }
}
