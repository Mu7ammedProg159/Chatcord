package com.mdev.chatcord.client.connection.websocket;

import com.mdev.chatcord.client.authentication.dto.HttpRequest;
import com.mdev.chatcord.client.user.service.User;
import lombok.Getter;


public class SocketClientHolder {
    @Getter
    private static WebSocketClientService instance;

    public static void init(String accessToken, User user) {
        instance = new WebSocketClientService(accessToken, user);
        instance.connectAndSubscribe();
    }

}