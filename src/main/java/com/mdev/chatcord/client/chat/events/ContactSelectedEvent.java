package com.mdev.chatcord.client.chat.events;

import com.mdev.chatcord.client.chat.direct.dto.PrivateChatDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ContactSelectedEvent extends ApplicationEvent {
    private final PrivateChatDTO contact;

    public ContactSelectedEvent(Object source, PrivateChatDTO contact) {
        super(source);
        this.contact = contact;
    }

}