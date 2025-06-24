package com.mdev.chatcord.client.connection.websocket.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mdev.chatcord.client.connection.websocket.configuration.StompSessionSubscriberHandler;
import com.mdev.chatcord.client.connection.websocket.demo.MessagesDTO;
import com.mdev.chatcord.client.connection.websocket.impl.WebSocketSubscriber;
import com.mdev.chatcord.client.exception.BusinessException;
import com.mdev.chatcord.client.friend.dto.AddFriendDTO;
import com.mdev.chatcord.client.friend.dto.ContactPreview;
import com.mdev.chatcord.client.friend.event.OnReceivedFriendship;
import com.mdev.chatcord.client.message.dto.MessageDTO;
import com.mdev.chatcord.client.user.dto.UserStatusDetails;
import com.mdev.chatcord.client.user.enums.EUserState;
import com.mdev.chatcord.client.user.service.User;
import javafx.application.Platform;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class WebSocketClientService {

    private final WebSocketStompClient stompClient;
    private StompSession session;
    private String accessToken;

    private final User user;

    private final ApplicationEventPublisher eventPublisher;

    public WebSocketClientService(String accessToken, User user, ApplicationEventPublisher eventPublisher) {
        this.accessToken = accessToken;
        this.user = user;
        this.eventPublisher = eventPublisher;

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(mapper);

        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.stompClient.setMessageConverter(converter);
    }

    public void connectAndSubscribe() {
        if (accessToken == null)
            throw new BusinessException("0002", "Failed to authenticate action.");

        String url = "ws://localhost:8080/ws/chatcord?access_token=" + accessToken;
        WebSocketHttpHeaders wsHeaders = new WebSocketHttpHeaders(); // Not used in this case
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("uuid", user.getUuid());

//        List<WebSocketSubscriber> subscribers = SubscriptionRegistrar.findAll(eventPublisher, user);
        //subscribers.forEach(sub -> sub.subscribe(session));

        StompSessionSubscriberHandler sessionSubscriberHandler = new StompSessionSubscriberHandler(eventPublisher, user){
            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                log.error("WebSocket error: {}", exception.getMessage());
                if (exception.getMessage().equalsIgnoreCase("Connection closed"))
                    reconnect(accessToken);
            }
        };


//        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
//            @Override
//            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
//                WebSocketClientService.this.session = session;
//                log.info("Connected to WebSocket");
//
//                session.subscribe("/user/queue/private", new StompFrameHandler() {
//                    @Override
//                    public Type getPayloadType(StompHeaders headers) {
//                        return MessageDTO.class;
//                    }
//
//                    @Override
//                    public void handleFrame(StompHeaders headers, Object payload) {
//                        MessageDTO message = (MessageDTO) payload;
//                        log.info("Message from {}: {}", message.getSender(), message.getContent());
//
//                        Platform.runLater(() -> {
//                            // 🔧 Replace with your UI controller method
//                            log.info("UI Display: {}", message.getContent());
//                        });
//                    }
//
//                });
//
//                session.subscribe("/user/queue/friendship.add", new StompFrameHandler() {
//                    @Override
//                    public Type getPayloadType(StompHeaders headers) {
//                        return ContactPreview.class;
//                    }
//
//                    @Override
//                    public void handleFrame(StompHeaders headers, Object payload) {
//                        ContactPreview contact = (ContactPreview) payload;
//                        log.info("{} requested friendship with you as [{}]", contact.getDisplayName(), user.getUsername());
//                        // update UI here
//                        Platform.runLater(() -> {
//                            eventPublisher.publishEvent(new OnReceivedFriendship(this, contact));
//                        });
//                    }
//                });
//                log.warn("Subscribed to /user/queue/friendship");
//
//                session.subscribe("/user/queue/status", new StompFrameHandler() {
//                    @Override
//                    public Type getPayloadType(StompHeaders headers) {
//                        return UserStatusDetails.class;
//                    }
//
//                    @Override
//                    public void handleFrame(StompHeaders headers, Object payload) {
//                        UserStatusDetails status = (UserStatusDetails) payload;
//                        log.info("{} is now {}", status.getUserUuid(), status.getState().name());
//                        // update UI here
//                    }
//                });
//                log.warn("Subscribed to /user/queue/status");
//
//                session.subscribe("/user/queue/messages", new StompFrameHandler() {
//                    @Override
//                    public Type getPayloadType(StompHeaders headers) {
//                        return MessagesDTO.class;
//                    }
//
//                    @Override
//                    public void handleFrame(StompHeaders headers, Object payload) {
//                        MessagesDTO message = (MessagesDTO) payload;
//                        log.info("Received From: {}, Sent to: {}, Content: {}", message.getFrom(), message.getTo(), message.getContent());
//                    }
//                });
//                log.warn("Subscribed to /user/queue/messages");
//            }
//
//            @Override
//            public void handleException(StompSession session, StompCommand command, StompHeaders headers,
//                                        byte[] payload, Throwable exception) {
//                log.error("❌ STOMP error: {}", exception.getMessage(), exception);
//            }
//
//            @Override
//            public void handleTransportError(StompSession session, Throwable exception) {
//                log.error("WebSocket error: {}", exception.getMessage());
//                if (exception.getMessage().equalsIgnoreCase("Connection closed"))
//                    reconnect(accessToken);
//            }
//        };

        CompletableFuture<StompSession> future = stompClient.connectAsync(url, wsHeaders, stompHeaders, sessionSubscriberHandler);

        future.thenAccept(stompSession -> {
            this.session = stompSession;
            log.info("Session established");
        }).exceptionally(ex -> {
            log.error("Failed to connect: {}", ex.getMessage());
            return null;
        });
    }

    public void reconnect(String newAccessToken) {
        try {
            session.disconnect(); // gracefully close if needed

        } catch (IllegalStateException e){
            this.accessToken = newAccessToken;
            connectAndSubscribe(); // 🔁 fresh session
            log.warn("WEBSOCKET RECONNECTED");
        }
    }

    public void sendPrivateMessage(MessageDTO messageDTO) {
        if (session != null && session.isConnected()) {
            session.send("/app/chat/direct/message.send", messageDTO);
            log.info("Sent to: {}", messageDTO.getReceiver());
        } else {
            log.error("Cannot send: session is null or disconnected. Trying to reconnect...");
            reconnect(accessToken);
        }
    }

    public void sendStatus(EUserState state) {
        if (session != null && session.isConnected()) {
            session.send("/app/users/status/change", state);
        }
    }

    public void sendFriendshipRequest(String username, String tag){
        if (session != null && session.isConnected()) {
            session.send("/app/friend.add", new AddFriendDTO(username, tag));
        } else {
            reconnect(accessToken);
            session.send("/app/friend.add", new AddFriendDTO(username, tag));
        }
    }

    // Debugging test for Websocket understanding.
    public void sendMessage(String from, String to, String content) {
        session.send("/app/chat", new MessagesDTO(from, to, content));
    }
}