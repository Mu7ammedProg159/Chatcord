package com.mdev.chatcord.client.connection.websocket.subscriptions;

import com.mdev.chatcord.client.chat.dto.ChatDTO;
import com.mdev.chatcord.client.connection.websocket.annotation.WebSocketSubscriber;
import com.mdev.chatcord.client.connection.websocket.impl.WebSocketRegistry;
import com.mdev.chatcord.client.connection.websocket.service.WebSocketFeatureSubscriber;
import com.mdev.chatcord.client.message.dto.MessageDTO;
import com.mdev.chatcord.client.message.event.OnMessageDelivered;
import com.mdev.chatcord.client.message.event.OnReceivedMessage;
import com.mdev.chatcord.client.user.service.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

@WebSocketSubscriber
public class MessageSubscriber extends WebSocketFeatureSubscriber {
    public MessageSubscriber(ApplicationEventPublisher eventPublisher, User user){
        register(WebSocketRegistry.DIRECT_MESSAGE_SEND, MessageDTO.class, message -> {
            if (user.getUuid().equalsIgnoreCase(message.getSender().getUuid())){
                eventPublisher.publishEvent(new OnMessageDelivered(this));
                log.info("The message: [{}] received [{}] successfully.", message.getContent(), message.getReceiver().getUsername());
            }
            else{
                eventPublisher.publishEvent(new OnReceivedMessage(this, message));
                log.info("You've sent a message: [{}] to [{}].", message.getContent(), message.getReceiver().getUsername());
            }
        });
    }
}
