package com.mdev.chatcord.client.connection.websocket.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;

@Component
@Slf4j
public class WebSocketEventLogger {

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();
        String dest = accessor.getDestination();
        log.warn("\uD83D\uDD14 Subscribed: {} by {}", dest, principal != null ? principal.getName() : "unknown");
    }
}