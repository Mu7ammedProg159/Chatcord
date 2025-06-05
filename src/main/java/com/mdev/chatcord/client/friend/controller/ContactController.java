package com.mdev.chatcord.client.friend.controller;

import com.mdev.chatcord.client.chat.enums.ChatType;
import com.mdev.chatcord.client.chat.events.ContactSelectedEvent;
import com.mdev.chatcord.client.common.implementation.TimeUtils;
import com.mdev.chatcord.client.common.service.SpringFXMLLoader;
import com.mdev.chatcord.client.chat.direct.controller.ChatController;
import com.mdev.chatcord.client.friend.dto.ContactPreview;
import com.mdev.chatcord.client.chat.direct.dto.PrivateChatDTO;
import com.mdev.chatcord.client.common.implementation.UIHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
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
public class ContactController implements UIHandler, TimeUtils {
    @FXML private OFxAvatarView contactImage;
    @FXML private Label chatName, tag;
    @FXML private Label lastChatMessage;
    @FXML private Label timestamp;
    @FXML private Label unseenMessagesCounter;
    @FXML private Label friendStatus;
    @FXML private HBox requestContainer;

    @Getter
    @FXML private ToggleButton contactBtn;

    @Getter
    @Setter
    private PrivateChatDTO privateChatDTO;

    private final SpringFXMLLoader springFXMLLoader;

    private final ChatController chatController;

    private final ApplicationEventPublisher eventPublisher;

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
            contactImage.setStatusVisible(false);
            tag.setManaged(false);
            tag.setVisible(false);
        }

        tag.setText("#" + contactPreview.getTag());

        if (contactPreview.getAvatarUrl() != null){
            contactImage.setUploadedImage(createImage(contactPreview.getAvatarUrl()));
        } else
            contactImage.setBackgroundColor(Color.web(contactPreview.getAvatarColor()));

        if (contactPreview.getLastMessage() != null && !contactPreview.getLastMessage()
                .equalsIgnoreCase("No Messages sent yet.")){
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
                }
                case PENDING -> {
                    friendStatus.setText(contactPreview.getFriendStatus().name());
                    friendStatus.getStyleClass().setAll("pendingFriendStatus");
                }
                case REQUESTED -> {
//                    friendStatus.setText(contactPreview.getFriendStatus().name());
                    setVisibility(false, friendStatus);
                    setVisibility(false, unseenMessagesCounter);
                    setVisibility(false, timestamp);
                    setVisibility(true, requestContainer);
                    //friendStatus.getStyleClass().setAll("requestedFriendStatus");
                }
                case DECLINED -> {
                    friendStatus.setText(contactPreview.getFriendStatus().name());
                    friendStatus.getStyleClass().setAll("declinedFriendStatus");
                }
        }


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

    private void isEverChatted(boolean flag) {
        setVisibility(flag, lastChatMessage);
        setVisibility(flag, unseenMessagesCounter);
        setVisibility(!flag, friendStatus);
    }

    public void onClick(PrivateChatDTO chat){
        eventPublisher.publishEvent(new ContactSelectedEvent(this, chat, chatType));
    }

    @FXML
    public void onChatBtnClicked(ActionEvent event){
//      onClick(privateChatDTO);
        log.info("You pressed {}'s contact.", contactPreview.getDisplayName());
    }
}
