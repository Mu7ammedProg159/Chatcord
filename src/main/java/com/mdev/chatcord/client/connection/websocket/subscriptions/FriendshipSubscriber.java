package com.mdev.chatcord.client.connection.websocket.subscriptions;

import com.mdev.chatcord.client.connection.websocket.annotation.WebSocketSubscriber;
import com.mdev.chatcord.client.connection.websocket.impl.WebSocketRegistry;
import com.mdev.chatcord.client.connection.websocket.service.WebSocketFeatureSubscriber;
import com.mdev.chatcord.client.friend.dto.ContactPreview;
import com.mdev.chatcord.client.friend.event.OnContactListUpdate;
import com.mdev.chatcord.client.friend.event.OnDeletedFriendship;
import com.mdev.chatcord.client.friend.event.OnReceivedFriendship;
import com.mdev.chatcord.client.user.service.User;
import org.springframework.context.ApplicationEventPublisher;

@WebSocketSubscriber
public class FriendshipSubscriber extends WebSocketFeatureSubscriber {
    public FriendshipSubscriber(ApplicationEventPublisher eventPublisher, User user){

        register(WebSocketRegistry.FRIEND_REQUEST, ContactPreview.class, contact -> {
            eventPublisher.publishEvent(new OnReceivedFriendship(this, contact));
            log.info("{} requested friendship with you as [{}]", contact.getDisplayName(), user.getUsername());
        });

        register(WebSocketRegistry.FRIEND_UPDATE, ContactPreview.class, contact -> {
            eventPublisher.publishEvent(new OnContactListUpdate(
                    this,
                    contact.getUuid().toString().toLowerCase(),
                    contact));
            String logStatus = contact.getFriendStatus().name().toLowerCase();
            log.info("{} {} friendship with you as [{}]", contact.getDisplayName(), logStatus, user.getUsername());
        });

        register(WebSocketRegistry.FRIEND_DELETE, String.class, uuid -> {
            eventPublisher.publishEvent(new OnDeletedFriendship(
                    this, uuid
            ));
            log.info("Friendship of {} has been removed.", uuid);
        });

        // Here you can add how many you want of friendship endpoints.
    }
}
