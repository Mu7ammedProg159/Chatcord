package com.mdev.chatcord.client.connection.websocket.subscriptions;

import com.mdev.chatcord.client.connection.websocket.annotation.WebSocketSubscriber;
import com.mdev.chatcord.client.connection.websocket.demo.MessagesDTO;
import com.mdev.chatcord.client.connection.websocket.impl.WebSocketRegistry;
import com.mdev.chatcord.client.connection.websocket.service.WebSocketFeatureSubscriber;
import com.mdev.chatcord.client.user.service.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

@WebSocketSubscriber
public class DemoMessageSubscriber extends WebSocketFeatureSubscriber {

    public DemoMessageSubscriber(ApplicationEventPublisher eventPublisher, User user){
        register(WebSocketRegistry.MESSAGE_SEND, MessagesDTO.class, message -> {
            log.info("Received From: {}, Sent to: {}, Content: {}", message.getFrom(), message.getTo(), message.getContent());
        });
    }

}
