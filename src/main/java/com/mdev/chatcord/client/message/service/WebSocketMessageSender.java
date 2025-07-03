package com.mdev.chatcord.client.message.service;

import com.mdev.chatcord.client.connection.websocket.controller.Communicator;
import org.springframework.stereotype.Component;

import com.mdev.chatcord.client.message.dto.MessageDTO;

@Component("PRIVATE")
public class WebSocketMessageSender implements MessageSender{
    @Override
    public void sendMessage(MessageDTO message) {
        Communicator.getInstance().sendDirectMessage(message);
    }
}
