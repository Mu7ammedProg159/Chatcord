package com.mdev.chatcord.client.connection.websocket;

import com.mdev.chatcord.client.authentication.dto.HttpRequest;
import com.mdev.chatcord.client.exception.BusinessException;
import com.mdev.chatcord.client.message.dto.MessageDTO;
import com.mdev.chatcord.client.user.dto.UserStatusDetails;
import com.mdev.chatcord.client.user.enums.EUserState;
import javafx.application.Platform;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public WebSocketClientService(String accessToken) {
        this.accessToken = accessToken;
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    public void connectAndSubscribe() {
        if (accessToken == null)
            throw new BusinessException("0002", "Failed to authenticate action.");

        String url = "ws://localhost:8080/ws/chatcord?access_token=" + accessToken;
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders(); // Not used in this case

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

                session.subscribe("/topic/messages", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return MessagesDTO.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        MessagesDTO message = (MessagesDTO) payload;
                        log.info("Received: From: {}, Content: {}", message.getFrom(), message.getContent());
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

        CompletableFuture<StompSession> future = stompClient.connectAsync(url, headers, sessionHandler);

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

    // Debugging test for Websocket understanding.
    public void sendMessage(String from, String content) {
        session.send("/app/chat", new MessagesDTO(from, content));
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class MessagesDTO{
    String from;
    String content;
}