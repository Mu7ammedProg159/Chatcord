package com.mdev.chatcord.client.message.service;

import com.mdev.chatcord.client.message.dto.MessageDTO;

public interface MessageSender {
    void sendMessage(MessageDTO message);
}
