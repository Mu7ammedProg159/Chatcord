package com.mdev.chatcord.client.connection.websocket.subscriptions;

import com.mdev.chatcord.client.common.implementation.TimeUtils;
import com.mdev.chatcord.client.connection.websocket.annotation.WebSocketSubscriber;
import com.mdev.chatcord.client.connection.websocket.impl.WebSocketRegistry;
import com.mdev.chatcord.client.connection.websocket.service.WebSocketFeatureSubscriber;
import com.mdev.chatcord.client.message.dto.MessageDTO;
import com.mdev.chatcord.client.message.event.OnMessageDelivered;
import com.mdev.chatcord.client.message.event.OnReceivedMessage;
import com.mdev.chatcord.client.user.service.User;
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

        register(WebSocketRegistry.UPDATE_MESSAGE_STATE, MessageDTO.class, messageDTO -> {
            log.info("{} seen the message you sent him at {}.", messageDTO.getReceiver().getUsername(),
                    TimeUtils.convertToLocalTime(messageDTO.getSeenAt()));
        });
    }
}
