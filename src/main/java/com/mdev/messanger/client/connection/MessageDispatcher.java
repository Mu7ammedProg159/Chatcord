package com.mdev.messanger.client.connection;

import com.mdev.messanger.client.dto.MessageDTO;

public interface MessageDispatcher {
    void onMessageReceived(MessageDTO receivedMessage);
}
