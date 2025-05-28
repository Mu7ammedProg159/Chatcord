package com.mdev.chatcord.client.chat.events;

import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class OnMessageSendEvent extends ApplicationEvent {


    public OnMessageSendEvent(Object source) {
        super(source);
    }
}
