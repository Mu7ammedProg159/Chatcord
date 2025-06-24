package com.mdev.chatcord.client.friend.event;

import com.mdev.chatcord.client.friend.dto.ContactPreview;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnReceivedFriendship extends ApplicationEvent {
    private ContactPreview contactPreview;
    public OnReceivedFriendship(Object source, ContactPreview contactPreview) {
        super(source);
        this.contactPreview = contactPreview;
    }
}
