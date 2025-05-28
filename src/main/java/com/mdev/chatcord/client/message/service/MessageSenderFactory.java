package com.mdev.chatcord.client.message.service;

import com.mdev.chatcord.client.chat.enums.ChatType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MessageSenderFactory {

    private final Map<String, MessageSender> strategyMap;

    public MessageSenderFactory(List<MessageSender> strategies){
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(s -> s.getClass().getAnnotation(Component.class).value(), s -> s));
    }

    public MessageSender getChatSender(ChatType chatType){
        return strategyMap.get(chatType.name());
    }


}
