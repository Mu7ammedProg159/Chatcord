package com.mdev.chatcord.client.chat.events;

import com.mdev.chatcord.client.chat.direct.dto.PrivateChatDTO;
import com.mdev.chatcord.client.chat.enums.ChatType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ContactSelectedEvent extends ApplicationEvent {
    private final PrivateChatDTO contact;
    private final ChatType chatType;

    public ContactSelectedEvent(Object source, PrivateChatDTO contact, ChatType chatType) {
        super(source);
        this.contact = contact;
        this.chatType = chatType;
    }

}