package com.mdev.chatcord.client.message.service;

import com.mdev.chatcord.client.connection.udp.ClientThread;
import com.mdev.chatcord.client.message.dto.MessageDTO;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("COMMUNITY")
@NoArgsConstructor
public class UDPMessageSender implements MessageSender{

    @Autowired
    private ClientThread clientThread;

    @Override
    public void sendMessage(MessageDTO message) {
        clientThread.sendMessage(message);
    }
}
