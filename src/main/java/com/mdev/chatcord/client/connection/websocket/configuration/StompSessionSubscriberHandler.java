package com.mdev.chatcord.client.connection.websocket.configuration;

import com.mdev.chatcord.client.connection.websocket.impl.WebSocketSubscriber;
import com.mdev.chatcord.client.connection.websocket.service.SubscriptionRegistrar;
import com.mdev.chatcord.client.user.service.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.util.List;

@Slf4j
public class StompSessionSubscriberHandler extends StompSessionHandlerAdapter {

    private final List<WebSocketSubscriber> subscribers;

    public StompSessionSubscriberHandler(ApplicationEventPublisher publisher, User user) {
        this.subscribers = SubscriptionRegistrar.findAll(publisher, user);
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        log.info("Connected. Auto-subscribing WebSocket handlers...");

        for (WebSocketSubscriber sub : subscribers) {
            sub.subscribe(session);
            log.info("Subscribed to {} successfully", sub.getClass().getName());
        }

        log.info("All WebSocket subscriptions registered.");
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers,
                                byte[] payload, Throwable exception) {
        log.error("STOMP error: {}", exception.getMessage(), exception);
    }

}
