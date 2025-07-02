package com.mdev.chatcord.client.connection.websocket.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mdev.chatcord.client.connection.websocket.configuration.StompSessionSubscriberHandler;
import com.mdev.chatcord.client.connection.websocket.demo.MessagesDTO;
import com.mdev.chatcord.client.exception.BusinessException;
import com.mdev.chatcord.client.friend.dto.ContactPreview;
import com.mdev.chatcord.client.friend.dto.FriendUser;
import com.mdev.chatcord.client.message.dto.MessageDTO;
import com.mdev.chatcord.client.user.enums.EUserState;
import com.mdev.chatcord.client.user.service.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

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

        CompositeMessageConverter converter = new CompositeMessageConverter(
                List.of(
                        new StringMessageConverter(),
                        new MappingJackson2MessageConverter(){{
                            setObjectMapper(mapper);
                        }}
                ));

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

    public void sendFriendshipRequest(ContactPreview receiverContact){
        if (session != null && session.isConnected()) {
            session.send("/app/friend.add", receiverContact);
        } else {
            reconnect(accessToken);
            session.send("/app/friend.add", receiverContact);
        }
    }

    public void changeFriendship(ContactPreview receiverContact) {
        if (session != null && session.isConnected()) {
            session.send("/app/friend.update", new FriendUser(receiverContact.getDisplayName(),
                    receiverContact.getTag()));
        } else {
            reconnect(accessToken);
            session.send("/app/friend.update", new FriendUser(receiverContact.getDisplayName(),
                    receiverContact.getTag()));
        }
    }

    // Debugging test for Websocket understanding.
    public void sendMessage(String from, String to, String content) {
        session.send("/app/chat", new MessagesDTO(from, to, content));
    }

    public void disconnect(){
        log.warn("WebSocket Session Disconnected.");
        session.disconnect();
    }
}
