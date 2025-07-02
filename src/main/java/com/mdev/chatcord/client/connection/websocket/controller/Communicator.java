package com.mdev.chatcord.client.connection.websocket.controller;

import com.mdev.chatcord.client.connection.websocket.service.WebSocketClientService;
import com.mdev.chatcord.client.user.service.User;
import lombok.Getter;
import org.springframework.context.ApplicationEventPublisher;


public class Communicator {
    @Getter
    private static WebSocketClientService instance;

    public static void init(String accessToken, User user, ApplicationEventPublisher applicationEventPublisher) {
        instance = new WebSocketClientService(accessToken, user, applicationEventPublisher);
        instance.connectAndSubscribe();
    }

    public static void destroy(){
        instance.disconnect();
        instance = null;
    }

}