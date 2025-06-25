package com.mdev.chatcord.client.friend.controller;

import com.mdev.chatcord.client.chat.enums.ChatType;
import com.mdev.chatcord.client.chat.events.ContactSelectedEvent;
import com.mdev.chatcord.client.common.implementation.EventStageHandler;
import com.mdev.chatcord.client.common.implementation.TimeUtils;
import com.mdev.chatcord.client.common.service.SpringFXMLLoader;
import com.mdev.chatcord.client.chat.direct.controller.ChatController;
import com.mdev.chatcord.client.connection.websocket.controller.Communicator;
import com.mdev.chatcord.client.friend.dto.ContactPreview;
import com.mdev.chatcord.client.chat.direct.dto.PrivateChatDTO;
import com.mdev.chatcord.client.common.implementation.UIHandler;
import com.mdev.chatcord.client.friend.enums.EFriendStatus;
import com.mdev.chatcord.client.friend.event.OnContactListUpdate;
import com.mdev.chatcord.client.friend.service.FriendService;
import com.mdev.chatcord.client.user.service.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.onyxfx.graphics.controls.OFxAvatarView;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(scopeName = "prototype")
@RequiredArgsConstructor
@Slf4j
public class ContactController implements UIHandler, TimeUtils, EventStageHandler {
    @FXML private OFxAvatarView contactImage;
    @FXML private Label chatName, tag;
    @FXML private Label lastChatMessage;
    @FXML private Label timestamp;
    @FXML private Label unseenMessagesCounter;
    @FXML private Label friendStatus;
    @FXML private HBox requestContainer;
    @FXML private Button acceptBtn, declineBtn;
    @FXML private AnchorPane test;
    @FXML private VBox rightSide;

    @Getter
    @FXML private ToggleButton contactBtn;

    @Getter
    @Setter
    private PrivateChatDTO privateChatDTO;

    private final SpringFXMLLoader springFXMLLoader;

    private final ChatController chatController;

    private final ApplicationEventPublisher eventPublisher;

    private final FriendService friendService;

    private final User user;

    private ChatType chatType;

    private ContactPreview contactPreview;

//    public void setCommunityChat(String communityName, String lastMessage,
//                                 LocalDateTime lastMessageDate){
//        chatName.setText(communityName);
//        contactImage.setUploadedImage(createImage("/images/CommunityIcon65x65.png"));
//        contactImage.setFitHeight(34);
//        contactImage.setFitWidth(36);
//        contactImage.setStatusVisible(false);
////        contactImage.setImage(createImage(groupAvatarUrl));
//        lastChatMessage.setText("No Messages yet.");
//        timestamp.setText(convertToLocalTime(LocalDateTime.now()));
//        this.chatType = ChatType.COMMUNITY;
//
//    }

    public void setData(ContactPreview contactPreview) {
        this.contactPreview = contactPreview;
        chatName.setText(contactPreview.getDisplayName());

        if (contactPreview.isGroup()){
            contactImage.setSize(50);
            contactImage.setStatusVisible(false);
            tag.setManaged(false);
            tag.setVisible(false);
            setVisibility(false, unseenMessagesCounter);
            rightSide.setAlignment(Pos.CENTER);
        }

        tag.setText("#" + contactPreview.getTag());

        showPreviewBasedOnDetails(contactPreview);

        try {
            timestamp.setText(convertToLocalTime(contactPreview.getLastMessageAt()));
        } catch (Exception e) {
            log.info(e.getMessage());
        }

//        contactImage.setUploadedImage(createImage(contactPreview.getAvatarUrl()==null ? "/images/CommunityIcon65x65.png" : contactPreview.getAvatarUrl()));

//        if (contactPreview.getUnreadStatus() != null)
//            if (contactPreview.getUnreadStatus().getUnreadCount() > 0) {
//                unseenMessagesCounter.setText(String.valueOf(chatInfo.getUnreadStatus().getUnreadCount()));
//                unseenMessagesCounter.setVisible(true);
//            } else {
//                unseenMessagesCounter.setText(String.valueOf(0));
//                unseenMessagesCounter.setVisible(false);
//            }
    }

    private void showPreviewBasedOnDetails(ContactPreview contactPreview) {
        if (contactPreview.getAvatarUrl() != null){
            contactImage.setUploadedImage(createImage(contactPreview.getAvatarUrl()));
        } else
            contactImage.setBackgroundColor(Color.web(contactPreview.getAvatarColor()));

        if (contactPreview.getLastMessage() != null && contactPreview.getFriendStatus().equals(EFriendStatus.ACCEPTED)){
            lastChatMessage.setText(contactPreview.getLastMessage());
            isEverChatted(true);
        }
        else{
            if (contactPreview.getFriendStatus() != null){
                isEverChatted(false);
            }
            lastChatMessage.setText("No messages sent yet.");
        }

        if (contactPreview.getFriendStatus() != null)
            switch (contactPreview.getFriendStatus()){
                case ACCEPTED -> {
                    friendStatus.setText(contactPreview.getFriendStatus().name());
                    friendStatus.getStyleClass().setAll("acceptedFriendStatus");
                    unseenMessagesCounter.setManaged(true);
                    unseenMessagesCounter.setVisible(false);
                    setVisibility(false, friendStatus);

                }
                case PENDING -> {
                    friendStatus.setText(contactPreview.getFriendStatus().name());
                    friendStatus.getStyleClass().setAll("pendingFriendStatus");
                }
                case REQUESTED -> {
                    setRequestedStyle(true);
                    contactBtn.addEventFilter(ActionEvent.ANY, event -> {
                        // If the click was on one of the child buttons, ignore it for the ToggleButton
                        if (!isInsideButton(contactBtn, (Node) event.getTarget())) {
                            event.consume(); // prevent the ToggleButton from toggling
                        }
                    });
                }
                case DECLINED -> {
                    friendStatus.setText(contactPreview.getFriendStatus().name());
                    friendStatus.getStyleClass().setAll("declinedFriendStatus");
                }
        }
    }

    private void setRequestedStyle(boolean flag) {
        contactBtn.getStyleClass().setAll("backgroundImage");
        setVisibility(!flag, friendStatus);
        setVisibility(!flag, unseenMessagesCounter);
        setVisibility(!flag, timestamp);
        setVisibility(flag, requestContainer);
        rightSide.setAlignment(Pos.CENTER);
    }

    private void isEverChatted(boolean flag) {
        setVisibility(flag, lastChatMessage);
        setVisibility(flag, unseenMessagesCounter);
        setVisibility(!flag, friendStatus);
    }

    private boolean isInsideButton(ToggleButton root, Node target) {
        while (target != null) {
            if (target instanceof Button && target != root) return true;
            if (target == root) return false;
            target = target.getParent();
        }
        return false;
    }

    public void onClick(PrivateChatDTO chat){
        eventPublisher.publishEvent(new ContactSelectedEvent(this, chat, chatType));
    }

    @FXML
    public void onChatBtnClicked(ActionEvent event){
//      onClick(privateChatDTO);
        if (contactPreview.getFriendStatus().equals(EFriendStatus.REQUESTED)){
            return;
        }
        log.info("You pressed {}'s contact.", contactPreview.getDisplayName());
    }

    @FXML
    public void onAcceptButtonClicked(ActionEvent event){
        friendService.acceptFriendship(contactPreview.getDisplayName(), contactPreview.getTag());
        contactPreview.setFriendStatus(EFriendStatus.ACCEPTED);
//        showPreviewBasedOnDetails(contactPreview);
        Communicator.getInstance().changeFriendship(contactPreview.getDisplayName(), contactPreview.getTag());
        eventPublisher.publishEvent(new OnContactListUpdate(this, String.valueOf(contactPreview.getUuid()), contactPreview));
        log.info("You've accepted {} friendship.", contactPreview.getDisplayName());
    }

    @FXML
    public void onDeclineButtonClicked(ActionEvent event){
        friendService.declineFriendship(contactPreview.getDisplayName(), contactPreview.getTag());
        contactPreview.setFriendStatus(EFriendStatus.DECLINED);
        Communicator.getInstance().changeFriendship(contactPreview.getDisplayName(), contactPreview.getTag());
        eventPublisher.publishEvent(new OnContactListUpdate(this, String.valueOf(contactPreview.getUuid()), contactPreview));
        log.info("You've declined {} friendship.", contactPreview.getDisplayName());
    }
}
