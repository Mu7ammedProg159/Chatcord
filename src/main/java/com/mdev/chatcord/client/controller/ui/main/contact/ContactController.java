package com.mdev.chatcord.client.controller.ui.main.contact;

import com.mdev.chatcord.client.component.SpringFXMLLoader;
import com.mdev.chatcord.client.controller.ui.main.ChatController;
import com.mdev.chatcord.client.controller.ui.main.MessageBubbleController;
import com.mdev.chatcord.client.dto.FriendContactDTO;
import com.mdev.chatcord.client.dto.FriendDTO;
import com.mdev.chatcord.client.dto.chat.ChatDTO;
import com.mdev.chatcord.client.dto.chat.PrivateChatDTO;
import com.mdev.chatcord.client.enums.EFriendStatus;
import com.mdev.chatcord.client.implementation.UIHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.IIOException;
import java.io.IOException;

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

    @FXML
    public void onChatBtnClicked(ActionEvent event){
        chatController.setChatDTO(privateChatDTO.getChatDTO());
    }
}
