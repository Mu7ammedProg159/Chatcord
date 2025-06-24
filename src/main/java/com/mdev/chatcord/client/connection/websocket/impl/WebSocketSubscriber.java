package com.mdev.chatcord.client.connection.websocket.impl;

import org.springframework.messaging.simp.stomp.StompSession;

public interface WebSocketSubscriber {
    void subscribe(StompSession session);
}
