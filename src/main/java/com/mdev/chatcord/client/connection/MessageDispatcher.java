package com.mdev.chatcord.client.connection;

import com.mdev.chatcord.client.dto.chat.MessageDTO;

public interface MessageDispatcher {
    void onMessageReceived(MessageDTO receivedMessage);
}
