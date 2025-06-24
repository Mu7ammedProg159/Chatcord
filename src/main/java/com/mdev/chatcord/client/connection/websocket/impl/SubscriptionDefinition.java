package com.mdev.chatcord.client.connection.websocket.impl;

import javafx.application.Platform;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.function.Consumer;

public class SubscriptionDefinition<T> {
    public final String destination;
    public final StompFrameHandler frameHandler;

    public SubscriptionDefinition(String destination, Class<T> payloadType, Consumer<T> handler) {
        this.destination = destination;
        this.frameHandler = new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return payloadType;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                Platform.runLater(() -> handler.accept(payloadType.cast(payload)));
            }
        };
    }
}
