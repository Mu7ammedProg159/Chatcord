package com.mdev.chatcord.client.connection.websocket.impl;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class WebSocketRegistry {
    private static final String dest = "/user";
    public static final String FRIEND_REQUEST = dest + "/queue/friendship.add";
    public static final String FRIEND_UPDATE = dest + "/queue/friendship.update";
    public static final String FRIEND_DELETE = dest + "/queue/friendship.delete";
    public static final String DIRECT_MESSAGE_SEND = dest + "/queue/private/message.send";
    public static final String UPDATE_MESSAGE_STATE = dest + "/queue/private/message.state";
    public static final String UPDATE_MESSAGE_STATE_BULK = dest + "/queue/private/message.state.bulk";
}
