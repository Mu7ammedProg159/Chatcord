package com.mdev.chatcord.client.chat.direct.controller;

import com.mdev.chatcord.client.chat.direct.dto.PrivateChatDTO;
import com.mdev.chatcord.client.chat.events.ContactSelectedEvent;
import com.mdev.chatcord.client.common.implementation.UIHandler;
import com.mdev.chatcord.client.common.service.SpringFXMLLoader;
import com.mdev.chatcord.client.connection.udp.ClientThread;
import com.mdev.chatcord.client.authentication.dto.HttpRequest;
import com.mdev.chatcord.client.message.controller.MessageBubbleController;
import com.mdev.chatcord.client.chat.dto.ChatDTO;
import com.mdev.chatcord.client.chat.dto.ChatMemberDTO;
import com.mdev.chatcord.client.message.dto.MessageDTO;
import com.mdev.chatcord.client.chat.enums.ChatType;
import com.mdev.chatcord.client.message.enums.EMessageStatus;
import com.mdev.chatcord.client.message.service.MessageSenderFactory;
import com.mdev.chatcord.client.user.dto.ProfileDetails;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
//@Scope(scopeName = "prototype")
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Slf4j
public class ChatController implements UIHandler {

    @FXML
    private Label chatTitle;
    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private VBox messagesContainer;
    @FXML
    private Button attachmentBtn, sendBtn;
    @FXML
    private TextField messageField;

    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @Autowired
    private ClientThread clientThread;

    @Autowired
    private HttpRequest httpRequest;

    @Autowired
    private MessageSenderFactory senderFactory;

    // This will be initialized and set when pressing into a friend from the friend list.

    private PrivateChatDTO chatPreview;

    private Image avatarImage;

    @Value("${spring.application.udp.server.port}")
    private int SERVER_PORT;
    @Value("${spring.application.udp.server.ip}")
    private String SERVER_IP;

    @FXML
    public void initialize() {
        clientThread.listen(dto -> {
            Platform.runLater(() -> {
                createReceiveMessage(dto);
            });
        });

        // Pre-message for UDP Connection Start
        sendMessage(new MessageDTO(ChatType.COMMUNITY, "__REGISTER__", new ChatMemberDTO("SERVER"),
                new ChatMemberDTO("Everyone"), LocalDateTime.now(), false, EMessageStatus.SENT));

        messagesContainer.heightProperty().addListener(
                (obs, oldVal, newVal) ->
                        chatScrollPane.setVvalue(
                                chatScrollPane.getVmax()));
    }

    // This for sending a message.
    public void sendMessage(MessageDTO messageDTO) {

        if (!messageDTO.getContent().isEmpty()) {
//
//            MessageDTO messageDTO = new MessageDTO(ChatType.COMMUNITY, message, from, to, System.currentTimeMillis(),
//                    false,
//                    EMessageStatus.SENT);
            //createReceiveMessage(messageDTO, avatarImage);

            senderFactory.getChatSender(messageDTO.getChatType()).sendMessage(messageDTO);
            messageField.clear();

        }
        if (!messageDTO.getChatType().equals(ChatType.COMMUNITY))
            createReceiveMessage(messageDTO);
    }

    @FXML
    public void onSendBtnClicked(ActionEvent event) {
        // This is only for Private CHat --- I MUST change the logic here later...
//        if (chatDTO == null)
//            sendMessage(httpRequest.getUserDTO().getUsername(), "All", httpRequest.getUserDTO().getPfpUrl());
//        else {
//            ChatMemberDTO sender = chatDTO.getChatMembersDto().get(0);
//            ChatMemberDTO receiver = chatDTO.getChatMembersDto().get(1);
//
//            sendMessage(sender.getUsername(), receiver.getUsername(), sender.getAvatarUrl());
//
//        }
        String message = messageField.getText();

        if (chatPreview.getChatDTO().getChatMembersDto().size() == 1)
            sendMessage(new MessageDTO(ChatType.valueOf(chatPreview.getChatDTO().getChatType()), message,
                    new ChatMemberDTO(httpRequest.getUserDTO().getUsername()),
                    new ChatMemberDTO("Everyone"),
                    LocalDateTime.now(), false, EMessageStatus.SENT));

        else
            sendMessage(new MessageDTO(ChatType.valueOf(chatPreview.getChatDTO().getChatType()), message,
                    chatPreview.getChatDTO().getChatMembersDto().get(0),
                    chatPreview.getChatDTO().getChatMembersDto().get(1),
                    LocalDateTime.now(), false, EMessageStatus.SENT));



    }

    public void addMessage(Node node) {
        messagesContainer.getChildren().add(node);
    }

    // This is needed to send and more importantly Receiving Messages.
    public void createReceiveMessage(MessageDTO dto) {
        try {
            FXMLLoader loader = springFXMLLoader.getLoader("/view/chat/message-view.fxml");
            Node messageNode = loader.load();
            MessageBubbleController controller = loader.getController();
            controller.setData(dto);
            //debugString = String.valueOf(controller.getUsername());

            log.info("Message Received as:  {}", dto.toString());

            if (!dto.getContent().equalsIgnoreCase("__REGISTER__"))
                addMessage(messageNode);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventListener
    public void onContactSelected(ContactSelectedEvent event) {
        loadChat(event.getContact(), event.getChatType());
    }

    public void loadChat(PrivateChatDTO chatDTO, ChatType chatType) {

        messagesContainer.getChildren().clear();
        this.chatPreview = chatDTO;

        // Community
        // You can use ChatType later...
        switch (chatType) {
            case COMMUNITY -> {
                chatTitle.setText("Welcome to Community Open Chat !");

            }
            case PRIVATE -> {
                chatTitle.setText("Chat with " + chatDTO.getFriendContactDTO().getName() +
                        "#" + chatDTO.getFriendContactDTO().getTag());

                if (chatDTO.getChatDTO().getMessages() != null)
                    for (MessageDTO message : chatDTO.getChatDTO().getMessages()) {
                        createReceiveMessage(message);
                    }
            }
        }
    }

    // TODO: Load the chat history for this contact
    // You'll probably want a `Map<String, List<MessageDTO>>` to simulate storage

}
