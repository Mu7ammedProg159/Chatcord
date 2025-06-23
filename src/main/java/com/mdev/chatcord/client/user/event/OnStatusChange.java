package com.mdev.chatcord.client.user.event;

import com.mdev.chatcord.client.user.dto.UserStatusDetails;
import org.springframework.context.ApplicationEvent;

public class OnStatusChange extends ApplicationEvent {
    private UserStatusDetails userStatusDetails;
    public OnStatusChange(Object source, UserStatusDetails userStatusDetails) {
        super(source);
        this.userStatusDetails = userStatusDetails;
    }
}
