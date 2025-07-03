package com.mdev.chatcord.client.connection.websocket.subscriptions;

import com.mdev.chatcord.client.connection.websocket.annotation.WebSocketSubscriber;
import com.mdev.chatcord.client.connection.websocket.impl.WebSocketRegistry;
import com.mdev.chatcord.client.connection.websocket.service.WebSocketFeatureSubscriber;
import com.mdev.chatcord.client.user.service.User;
import org.springframework.context.ApplicationEventPublisher;


public class DemoMessageSubscriber extends WebSocketFeatureSubscriber {

//    public DemoMessageSubscriber(ApplicationEventPublisher eventPublisher, User user){
//        register(WebSocketRegistry.DIRECT_MESSAGE_SEND, MessagesDTO.class, message -> {
//            log.info("Received From: {}, Sent to: {}, Content: {}", message.getFrom(), message.getTo(), message.getContent());
//        });
//    }

}
