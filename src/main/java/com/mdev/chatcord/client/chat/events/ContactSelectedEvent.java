package com.mdev.chatcord.client.chat.events;

import com.mdev.chatcord.client.chat.direct.dto.PrivateChatDTO;
import com.mdev.chatcord.client.chat.dto.ChatDTO;
import com.mdev.chatcord.client.chat.enums.ChatType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ContactSelectedEvent extends ApplicationEvent {
    //private final ChatDTO contact;
    private final String uuid;
    private final ChatType chatType;

    public ContactSelectedEvent(Object source, String uuid, ChatType chatType) {
        super(source);
        this.uuid = uuid;
        //this.contact = contact;
        this.chatType = chatType;
    }

}