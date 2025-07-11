package com.mdev.chatcord.client.friend.controller;

import com.mdev.chatcord.client.chat.direct.controller.ChatController;
import com.mdev.chatcord.client.chat.dto.ChatMemberDTO;
import com.mdev.chatcord.client.common.service.SpringFXMLLoader;
import com.mdev.chatcord.client.common.implementation.TimeUtils;
import com.mdev.chatcord.client.common.implementation.UIErrorHandler;
import com.mdev.chatcord.client.friend.dto.ContactPreview;
import com.mdev.chatcord.client.friend.dto.Loader;
import com.mdev.chatcord.client.friend.enums.EFriendStatus;
import com.mdev.chatcord.client.friend.event.OnContactListUpdate;
import com.mdev.chatcord.client.friend.event.OnDeletedFriendship;
import com.mdev.chatcord.client.friend.event.OnReceivedFriendship;
import com.mdev.chatcord.client.friend.service.FriendService;
import com.mdev.chatcord.client.message.enums.EMessageStatus;
import com.mdev.chatcord.client.message.event.OnReceivedMessage;
import com.mdev.chatcord.client.message.service.MessageService;
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
import javafx.scene.paint.Color;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendsController implements UIErrorHandler {

    @FXML private Button addContactButton;
    @FXML private TextField searchField;
    @FXML private VBox directChatList;
    @FXML private VBox contactsListView;
    //@FXML private OFxExpandablePane directChatList;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @Autowired
    private FriendService friendService;

    @Autowired
    private MessageService messageService;

    private StackPane mainOverlayPane;

    private ToggleGroup toggleGroup = new ToggleGroup();

    private ChatMemberDTO sender;

    private Map<String, Loader<ContactController>> contacts = new HashMap<>();

    @FXML
    public void initialize() {
        addCommunityContact();
        reloadContacts();
    }

    public void reloadContacts() {
        // This retrieve all the friends that we asked to add and the ones we added.
        List<ContactPreview> allFriends = friendService.getAllFriends();
        if (allFriends != null)
            for (ContactPreview friend: allFriends){
                addFriendContact(friend);
            }
    }

    @EventListener
    public void onFriendshipRequested(OnReceivedFriendship onReceivedFriendship){
        logger.warn("Received Websocket event for adding new friendship request.");
        addFriendContact(onReceivedFriendship.getContactPreview());
    }

    @EventListener
    public void onUpdate(OnContactListUpdate onContactListUpdate){
        Node refreshNode = contacts.get(onContactListUpdate.getContactUuid()).getNode();
        directChatList.getChildren().remove(refreshNode);
        addFriendContact(onContactListUpdate.getContactPreview());
    }

    @EventListener
    public void onRemoveFriendship(OnDeletedFriendship onDeletedFriendship){
        Node declinedNode = contacts.get(onDeletedFriendship.getContactUUID()).getNode();
        directChatList.getChildren().remove(declinedNode);
    }

    @EventListener
    public void onMessageReceived(OnReceivedMessage onReceivedMessage){
        ContactController controller = contacts.get(onReceivedMessage.getMessage().getSender().getUuid()).getController();
        controller.getLastChatMessage().setText(onReceivedMessage.getMessage().getContent());
        controller.getTimestamp().setText(TimeUtils.convertToLocalTime(onReceivedMessage.getMessage().getSentAt()));

        if(!controller.getContactBtn().isSelected()){
            controller.getUnseenMessagesCounter().setText(String.valueOf(controller.incrementMessageCounter()));
            setVisibility(true, controller.getUnseenMessagesCounter());
        }
        else {
            messageService.changeMessageStatus(onReceivedMessage.getMessage(), EMessageStatus.SEEN);
            logger.info("You saw the message {}.", onReceivedMessage.getMessage().getContent());
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

        //controller.setCommunityChat("Community", "/images/CommunityIcon65x65.png",
//                "There are no messages yet.", LocalDateTime.now());
        ContactPreview contactPreview = new ContactPreview(
                UUID.randomUUID(), "Community", null,
                "/images/CommunityIcon65x65.png", Color.LIMEGREEN.toString(),
                null, 0, true, null,
                LocalDateTime.of(2025, 7, 11, 6, 41));

        controller.setData(contactPreview);

        controller.getContactBtn().setToggleGroup(toggleGroup);
        contactsListView.getChildren().add(0, root);
    }

    private void addFriendContact(ContactPreview contactPreview) {
        FXMLLoader loader = springFXMLLoader.getLoader("/view/chat/contact-view.fxml");
        Node root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ContactController controller = loader.getController();

        controller.setData(contactPreview);

        controller.getContactBtn().setToggleGroup(toggleGroup);

        contacts.put(String.valueOf(contactPreview.getUuid()), new Loader<ContactController>(root, controller));
        directChatList.getChildren().add(root);
    }

//    public ContactPreview getContact(String username, String tag) throws IOException {
//        FXMLLoader loader = springFXMLLoader.getLoader("/view/chat/contact-view.fxml");
//        Parent root = loader.load();
//        ContactController controller = loader.getController();
//
//        ContactPreview contactDetails = friendService.getFriend(username, tag);
//
//        controller.setData(contactDetails);
//
//        controller.getContactBtn().setToggleGroup(toggleGroup);
//
//        directChatList.getChildren().add(root);
//
//        return contactDetails;
//    }

    @FXML
    public void onAddContactClick(ActionEvent event) {
        try {
            FXMLLoader loader = springFXMLLoader.getLoader("/view/chat/addContactPopup-view.fxml");
            Parent root = loader.load();
            AddContactController controller = loader.getController();

            controller.setContactList(directChatList);

            mainOverlayPane.getChildren().add(root);

            controller.setOnRetrieveContact(contactPreview -> {
                try {
                    Platform.runLater(()->{
                        addFriendContact(contactPreview);
                        mainOverlayPane.getChildren().remove(root);
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
