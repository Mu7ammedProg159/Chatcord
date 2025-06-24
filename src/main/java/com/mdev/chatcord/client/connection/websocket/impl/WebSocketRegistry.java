package com.mdev.chatcord.client.connection.websocket.impl;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class WebSocketRegistry {
    private static final String dest = "/user";
    public static final String FRIEND_REQUEST = dest + "/queue/friendship.add";
    public static final String FRIEND_ACCEPT = dest + "/queue/friendship.accept";
    public static final String MESSAGE_SEND = dest + "/queue/messages";
}
