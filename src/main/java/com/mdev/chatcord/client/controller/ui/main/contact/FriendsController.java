package com.mdev.chatcord.client.controller.ui.main.contact;

import com.mdev.chatcord.client.component.SpringFXMLLoader;
import com.mdev.chatcord.client.controller.ui.main.ChatController;
import com.mdev.chatcord.client.dto.FriendDTO;
import com.mdev.chatcord.client.implementation.TimeUtil;
import com.mdev.chatcord.client.implementation.UIErrorHandler;
import com.mdev.chatcord.client.implementation.UIHandler;
import com.mdev.chatcord.client.service.FriendService;
import com.mdev.chatcord.client.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendsController implements TimeUtil, UIErrorHandler {

    @FXML private Button addContactButton;
    @FXML private TextField searchField;
    @FXML private VBox contactsListView;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @Autowired
    private FriendService friendService;

    private StackPane mainOverlayPane;

    @FXML
    public void initialize(){

    }

    public void reloadContacts(){
        List<FriendDTO> allFriends = friendService.getAllFriends();
        for (int i=0; i > 0; i++){

        }
    }

    @FXML
    public void onAddContactClick(ActionEvent event) {
        try {
            FXMLLoader loader = springFXMLLoader.getLoader("/view/main-layout/addContactPopup-view.fxml");
            Parent root = loader.load();
            AddContactController controller = loader.getController();

            controller.setContactList(contactsListView);

            mainOverlayPane.getChildren().add(root);

            controller.setOnClose(() -> {
                mainOverlayPane.getChildren().remove(root);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
