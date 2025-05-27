package com.mdev.chatcord.client.connection;

import com.mdev.chatcord.client.message.dto.MessageDTO;

public interface MessageDispatcher {
    void onMessageReceived(MessageDTO receivedMessage);
}
