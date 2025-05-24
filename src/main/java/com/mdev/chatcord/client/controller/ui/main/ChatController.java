package com.mdev.chatcord.client.controller.ui.main;

import com.mdev.chatcord.client.connection.ClientThread;
import com.mdev.chatcord.client.dto.chat.MessageDTO;
import com.mdev.chatcord.client.enums.EMessageStatus;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatController {

    @FXML private Label chatTitle;
    @FXML private ScrollPane chatScrollPane;
    @FXML private VBox messagesContainer;
    @FXML private Button attachmentBtn, sendBtn;
    @FXML private TextField messageField;

    public void setData(String username, String tag){
        chatTitle.setText("Welcome, " + username + "#" + tag);
    }

    public void addMessage(Node node){
        messagesContainer.getChildren().add(node);
    }

    public void sendMessage(ClientThread clientThread, String username, String profileImageURL) {
        String message = messageField.getText();

        if (!message.isEmpty()){

            MessageDTO messageDTO = new MessageDTO(username, message, System.currentTimeMillis(),
                    EMessageStatus.SENT); //Node messageNode = createMessageNode(username, message, pfpImage);

            clientThread.sendMessage(messageDTO);
            messageField.clear();
        }
    }

    private void loadChat(String contactId) {

        messagesContainer.getChildren().clear();
        chatTitle.setText("Chat with " + contactId);

        // TODO: Load the chat history for this contact
        // You'll probably want a `Map<String, List<MessageDTO>>` to simulate storage
    }

}
