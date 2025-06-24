package com.mdev.chatcord.client.connection.websocket.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessagesDTO{
    String from;
    String to;
    String content;
}