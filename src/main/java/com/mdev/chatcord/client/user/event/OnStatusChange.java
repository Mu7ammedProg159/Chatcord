package com.mdev.chatcord.client.user.event;

import com.mdev.chatcord.client.user.dto.UserStatusDetails;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class OnStatusChange extends ApplicationEvent {
    private final UserStatusDetails userStatusDetails;
    public OnStatusChange(Object source, UserStatusDetails userStatusDetails) {
        super(source);
        this.userStatusDetails = userStatusDetails;
    }
}
