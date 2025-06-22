package com.mdev.chatcord.client.friend.event;

import com.mdev.chatcord.client.friend.controller.ContactController;
import com.mdev.chatcord.client.friend.dto.ContactPreview;
import javafx.scene.Node;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnContactListUpdate extends ApplicationEvent {
    private final String contactUuid;
    private final ContactPreview contactPreview;
    public OnContactListUpdate(Object source, String contactUuid, ContactPreview contactPreview) {
        super(source);
        this.contactUuid = contactUuid;
        this.contactPreview = contactPreview;
    }
}
