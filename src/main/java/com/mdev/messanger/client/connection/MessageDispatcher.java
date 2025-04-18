package com.mdev.messanger.client.connection;

public interface MessageDispatcher {
    void onMessageReceived(MessageDTO receivedMessage);
}
