package com.mdev.chatcord.client.friend.controller;

import com.mdev.chatcord.client.chat.events.ContactSelectedEvent;
import com.mdev.chatcord.client.common.service.SpringFXMLLoader;
import com.mdev.chatcord.client.chat.direct.controller.ChatController;
import com.mdev.chatcord.client.friend.dto.FriendContactDTO;
import com.mdev.chatcord.client.chat.ChatDTO;
import com.mdev.chatcord.client.chat.direct.dto.PrivateChatDTO;
import com.mdev.chatcord.client.common.implementation.UIHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContactController implements UIHandler {
    @FXML private ImageView contactImage;
    @FXML private Label chatName;
    @FXML private Label lastChatMessage;
    @FXML private Label timestamp;
    @FXML private Label unseenMessagesCounter;
    @FXML private Label friendStatus;

    @Getter
    @Setter
    private PrivateChatDTO privateChatDTO;

    private final SpringFXMLLoader springFXMLLoader;

    private final ChatController chatController;

    private final ApplicationEventPublisher eventPublisher;

    public void setData(PrivateChatDTO privateChat) {

        this.privateChatDTO = privateChat;
        FriendContactDTO friendInfo = privateChat.getFriendContactDTO();
        ChatDTO chatInfo = privateChat.getChatDTO();

        chatName.setText(friendInfo.getName());
        if (chatInfo.getLastMessage() != null)
            lastChatMessage.setText(chatInfo.getLastMessage());
        else{
            setVisibility(false, lastChatMessage);
            //lastChatMessage.setText("No messages sent yet.");
            setVisibility(true, friendStatus);
        }

        switch (chatInfo.getRelationship()){
            case ACCEPTED -> {
                friendStatus.setText(friendInfo.getFriendStatus().name());
                friendStatus.getStyleClass().setAll("acceptedFriendStatus");
            }
            case PENDING -> {
                friendStatus.setText(friendInfo.getFriendStatus().name());
                friendStatus.getStyleClass().setAll("pendingFriendStatus");
            }
            case REQUESTED -> {
                friendStatus.setText(friendInfo.getFriendStatus().name());
                friendStatus.getStyleClass().setAll("requestedFriendStatus");
            }
            case DECLINED -> {
                friendStatus.setText(friendInfo.getFriendStatus().name());
                friendStatus.getStyleClass().setAll("declinedFriendStatus");
            }
        }


        try {
            timestamp.setText(chatInfo.getLastMessageAt().toString());
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        Image img = new Image(getClass().getResource(friendInfo.getProfilePictureURL()).toExternalForm());
        contactImage.setImage(img);

        if (chatInfo.getUnreadStatus().getUnreadCount() > 0) {
            unseenMessagesCounter.setText(String.valueOf(chatInfo.getUnreadStatus().getUnreadCount()));
            unseenMessagesCounter.setVisible(true);
        } else {
            unseenMessagesCounter.setText(String.valueOf(0));
            unseenMessagesCounter.setVisible(false);
        }
    }

    public void onClick(PrivateChatDTO chat){
        eventPublisher.publishEvent(new ContactSelectedEvent(this, chat));
    }

    @FXML
    public void onChatBtnClicked(ActionEvent event){
        onClick(privateChatDTO);
    }
}
