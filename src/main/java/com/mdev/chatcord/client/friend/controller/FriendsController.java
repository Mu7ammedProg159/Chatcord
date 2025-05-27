package com.mdev.chatcord.client.friend.controller;

import com.mdev.chatcord.client.common.service.SpringFXMLLoader;
import com.mdev.chatcord.client.chat.direct.dto.PrivateChatDTO;
import com.mdev.chatcord.client.common.implementation.TimeUtils;
import com.mdev.chatcord.client.common.implementation.UIErrorHandler;
import com.mdev.chatcord.client.friend.service.FriendService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendsController implements TimeUtils, UIErrorHandler {

    @FXML private Button addContactButton;
    @FXML private TextField searchField;
    @FXML private VBox contactsListView;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @Autowired
    private FriendService friendService;

    private StackPane mainOverlayPane;

    private ToggleGroup toggleGroup;

    @FXML
    public void initialize() {
        addCommunityContact();
        reloadContacts();
    }

    public void reloadContacts() {

        // This retrieve all the request from others to us asking for friendship.
        List<PrivateChatDTO> allPendingFriends = friendService.getAllPendingFriends();
        if (allPendingFriends != null)
            for (PrivateChatDTO pendingFriend: allPendingFriends) {
                addFriendContact(pendingFriend);
            }

        // This retrieve all the friends that we asked to add and the ones we added.
        List<PrivateChatDTO> allFriends = friendService.getAllFriends();
        if (allFriends != null)
            for (PrivateChatDTO friend: allFriends){
                addFriendContact(friend);
            }
    }

    private void addCommunityContact() {
        FXMLLoader loader = springFXMLLoader.getLoader("/view/chat/contact-view.fxml");
        Node root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ContactController controller = loader.getController();

        controller.setCommunityChat("Community", "/images/CommunityIcon65x65.png",
                "There are no messages yet.", LocalDateTime.now());
        controller.getContactBtn().setToggleGroup(toggleGroup);
        contactsListView.getChildren().add(0, root);
    }

    private void addFriendContact(PrivateChatDTO privateChat) {
        FXMLLoader loader = springFXMLLoader.getLoader("/view/chat/contact-view.fxml");
        Node root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ContactController controller = loader.getController();

        controller.setData(privateChat);

        controller.getContactBtn().setToggleGroup(toggleGroup);

        contactsListView.getChildren().add(root);
    }

    public PrivateChatDTO getContact(String username, String tag) throws IOException {
        FXMLLoader loader = springFXMLLoader.getLoader("/view/chat/contact-view.fxml");
        Parent root = loader.load();
        ContactController controller = loader.getController();

        PrivateChatDTO contactDetails = friendService.getFriend(username, tag);

        controller.setData(contactDetails);

        controller.getContactBtn().setToggleGroup(toggleGroup);

        contactsListView.getChildren().add(root);

        return contactDetails;
    }

    @FXML
    public void onAddContactClick(ActionEvent event) {
        try {
            FXMLLoader loader = springFXMLLoader.getLoader("/view/chat/addContactPopup-view.fxml");
            Parent root = loader.load();
            AddContactController controller = loader.getController();

            controller.setContactList(contactsListView);

            mainOverlayPane.getChildren().add(root);

            controller.setOnRetrieveContact((username, tag) -> {
                try {
                    Platform.runLater(()->{
                        try {
                            getContact(username, tag);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            controller.setOnClose(() -> {
                mainOverlayPane.getChildren().remove(root);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
