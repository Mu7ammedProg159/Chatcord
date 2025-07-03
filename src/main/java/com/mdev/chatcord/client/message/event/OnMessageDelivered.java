package com.mdev.chatcord.client.message.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnMessageDelivered extends ApplicationEvent {
    public OnMessageDelivered(Object source) {
        super(source);
    }
}
