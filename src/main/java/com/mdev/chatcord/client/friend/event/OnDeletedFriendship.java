package com.mdev.chatcord.client.friend.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnDeletedFriendship extends ApplicationEvent {
    private final String contactUUID;
    public OnDeletedFriendship(Object source, String contactUUID) {
        super(source);
        this.contactUUID = contactUUID;
    }
}
