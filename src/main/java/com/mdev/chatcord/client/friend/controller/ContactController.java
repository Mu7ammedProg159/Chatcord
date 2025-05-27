package com.mdev.chatcord.client.friend.controller;

import com.mdev.chatcord.client.chat.enums.ChatType;
import com.mdev.chatcord.client.chat.events.ContactSelectedEvent;
import com.mdev.chatcord.client.common.implementation.TimeUtils;
import com.mdev.chatcord.client.common.service.SpringFXMLLoader;
import com.mdev.chatcord.client.chat.direct.controller.ChatController;
import com.mdev.chatcord.client.friend.dto.FriendContactDTO;
import com.mdev.chatcord.client.chat.dto.ChatDTO;
import com.mdev.chatcord.client.chat.direct.dto.PrivateChatDTO;
import com.mdev.chatcord.client.common.implementation.UIHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Scope(scopeName = "prototype")
@RequiredArgsConstructor
@Slf4j
public class ContactController implements UIHandler, TimeUtils {
    @FXML private ImageView contactImage;
    @FXML private Label chatName;
    @FXML private Label lastChatMessage;
    @FXML private Label timestamp;
    @FXML private Label unseenMessagesCounter;
    @FXML private Label friendStatus;

    @Getter
    @FXML private ToggleButton contactBtn;

    @Getter
    @Setter
    private PrivateChatDTO privateChatDTO;

    private final SpringFXMLLoader springFXMLLoader;

    private final ChatController chatController;

    private final ApplicationEventPublisher eventPublisher;

    private ChatType chatType;

    public void setCommunityChat(String communityName, String groupAvatarUrl, String lastMessage,
                                 LocalDateTime lastMessageDate){
        chatName.setText(communityName);
        contactImage.setImage(createImage(groupAvatarUrl));
        lastChatMessage.setText(lastMessage);
        timestamp.setText(convertToLocalTime(lastMessageDate));
        this.chatType = ChatType.COMMUNITY;

    }

    public void setData(PrivateChatDTO privateChat) {

        this.privateChatDTO = privateChat;
        FriendContactDTO friendInfo = privateChat.getFriendContactDTO();
        ChatDTO chatInfo = privateChat.getChatDTO();
        this.chatType = ChatType.valueOf(privateChat.getChatDTO().getChatType());

        chatName.setText(friendInfo.getName());
        if (chatInfo.getLastMessage() != null)
            lastChatMessage.setText(chatInfo.getLastMessage());
        else{
            setVisibility(false, lastChatMessage);
            //lastChatMessage.setText("No messages sent yet.");
            setVisibility(true, friendStatus);
        }

        switch (friendInfo.getFriendStatus()){
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
            timestamp.setText(convertToLocalTime(chatInfo.getLastMessageAt()));
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
        eventPublisher.publishEvent(new ContactSelectedEvent(this, chat, chatType));
    }

    @FXML
    public void onChatBtnClicked(ActionEvent event){
        switch (chatType){
            case COMMUNITY -> {
                onClick(null);
            }
            case PRIVATE -> {
                onClick(privateChatDTO);
            }
        }
    }
}
