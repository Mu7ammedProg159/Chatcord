package com.mdev.chatcord.client.connection.websocket;

import com.mdev.chatcord.client.authentication.dto.HttpRequest;
import com.mdev.chatcord.client.exception.BusinessException;
import com.mdev.chatcord.client.friend.dto.AddFriendDTO;
import com.mdev.chatcord.client.friend.dto.ContactPreview;
import com.mdev.chatcord.client.message.dto.MessageDTO;
import com.mdev.chatcord.client.user.dto.UserStatusDetails;
import com.mdev.chatcord.client.user.enums.EUserState;
import com.mdev.chatcord.client.user.service.User;
import javafx.application.Platform;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class WebSocketClientService {

    private final WebSocketStompClient stompClient;
    private StompSession session;
    private String accessToken;

    private final User user;

    public WebSocketClientService(String accessToken, User user) {
        this.accessToken = accessToken;
        this.user = user;
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    public void connectAndSubscribe() {
        if (accessToken == null)
            throw new BusinessException("0002", "Failed to authenticate action.");

        String url = "ws://localhost:8080/ws/chatcord?access_token=" + accessToken;
        WebSocketHttpHeaders wsHeaders = new WebSocketHttpHeaders(); // Not used in this case
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("uuid", user.getUuid());

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                WebSocketClientService.this.session = session;
                log.info("Connected to WebSocket");

                session.subscribe("/user/queue/private", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return MessageDTO.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        MessageDTO message = (MessageDTO) payload;
                        log.info("Message from {}: {}", message.getSender(), message.getContent());

                        Platform.runLater(() -> {
                            // 🔧 Replace with your UI controller method
                            log.info("UI Display: {}", message.getContent());
                        });
                    }
                });

                session.subscribe("/user/queue/friendship", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return ContactPreview.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        ContactPreview contact = (ContactPreview) payload;
                        log.info("{} requested friendship with {}", user.getUsername(), contact.getDisplayName());
                        // update UI here
                    }
                });

                session.subscribe("/user/queue/status", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return UserStatusDetails.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        UserStatusDetails status = (UserStatusDetails) payload;
                        log.info("{} is now {}", status.getUserUuid(), status.getState().name());
                        // update UI here
                    }
                });

                session.subscribe("/user/queue/messages", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return MessagesDTO.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        MessagesDTO message = (MessagesDTO) payload;
                        log.info("Received From: {}, Sent to: {}, Content: {}", message.getFrom(), message.getTo(), message.getContent());
                    }
                });
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                log.error("WebSocket error: {}", exception.getMessage());
                if (exception instanceof IllegalStateException)
                    reconnect(accessToken);
            }
        };

        CompletableFuture<StompSession> future = stompClient.connectAsync(url, wsHeaders, stompHeaders, sessionHandler);

        future.thenAccept(stompSession -> {
            this.session = stompSession;
            log.info("Session established");
        }).exceptionally(ex -> {
            log.error("Failed to connect: {}", ex.getMessage());
            return null;
        });
    }

    public void reconnect(String newAccessToken) {
        session.disconnect(); // gracefully close if needed
        this.accessToken = newAccessToken;
        connectAndSubscribe(); // 🔁 fresh session
        log.warn("WEBSOCKET RECONNECTED");
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
        session.send("/app/add", new AddFriendDTO(username, tag));
    }

    // Debugging test for Websocket understanding.
    public void sendMessage(String from, String to, String content) {
        session.send("/app/chat", new MessagesDTO(from, to, content));
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class MessagesDTO{
    String from;
    String to;
    String content;
}