package com.mdev.chatcord.client.connection.websocket.service;

import com.mdev.chatcord.client.connection.websocket.impl.SubscriptionDefinition;
import com.mdev.chatcord.client.connection.websocket.impl.WebSocketSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompSession;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class WebSocketFeatureSubscriber implements WebSocketSubscriber {
    public final Logger log = LoggerFactory.getLogger(getClass());
    protected final List<SubscriptionDefinition<?>> subscriptions = new ArrayList<>();

    @Override
    public void subscribe(StompSession session) {
        for (SubscriptionDefinition<?> def : subscriptions) {
            session.subscribe(def.destination, def.frameHandler);
        }
    }

    protected <T> void register(String destination, Class<T> payloadType, Consumer<T> handler) {
        subscriptions.add(new SubscriptionDefinition<>(destination, payloadType, handler));
    }

}
