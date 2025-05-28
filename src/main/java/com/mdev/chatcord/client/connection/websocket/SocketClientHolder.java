package com.mdev.chatcord.client.connection.websocket;

import com.mdev.chatcord.client.authentication.dto.HttpRequest;
import lombok.Getter;

public class SocketClientHolder {
    @Getter
    private static WebSocketClientService instance;

    public static void init(String accessToken) {
        instance = new WebSocketClientService(accessToken);
        instance.connectAndSubscribe();
    }

}