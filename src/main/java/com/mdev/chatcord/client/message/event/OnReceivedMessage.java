package com.mdev.chatcord.client.message.event;

import com.mdev.chatcord.client.message.dto.MessageDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnReceivedMessage extends ApplicationEvent {
    private final MessageDTO message;
    public OnReceivedMessage(Object source, MessageDTO message) {
        super(source);
        this.message = message;
    }
}
