package com.mdev.chatcord.client.message.event;

import org.springframework.context.ApplicationEvent;

public class OnSeenMessages extends ApplicationEvent {
    public OnSeenMessages(Object source) {
        super(source);
    }
}
