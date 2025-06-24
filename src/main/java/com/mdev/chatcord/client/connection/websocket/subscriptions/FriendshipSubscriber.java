package com.mdev.chatcord.client.connection.websocket.subscriptions;

import com.mdev.chatcord.client.connection.websocket.annotation.WebSocketSubscriber;
import com.mdev.chatcord.client.connection.websocket.impl.WebSocketRegistry;
import com.mdev.chatcord.client.connection.websocket.service.WebSocketFeatureSubscriber;
import com.mdev.chatcord.client.friend.dto.ContactPreview;
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

        register(WebSocketRegistry.FRIEND_ACCEPT, ContactPreview.class, contact -> {
            eventPublisher.publishEvent(new OnReceivedFriendship(this, contact));}
        );

        // Here you can add how many you want of friendship endpoints.
    }
}
