package com.mdev.chatcord.client.message.dto;

import com.mdev.chatcord.client.chat.enums.ChatType;
import com.mdev.chatcord.client.message.enums.EMessageStatus;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MessageDTO implements Serializable {

    private ChatType chatType;
    private String content;
    private String sender; // uuid
    private String receiver; // Can be username#tag or guildId << this is currently is the group.
    private long timestamp;
    private boolean isEdited;
    private EMessageStatus messageStatus;

    public MessageDTO(String content, String sender, long timestamp, EMessageStatus messageStatus) {
        this.content = content;
        this.sender = sender;
        this.timestamp = timestamp;
        this.messageStatus = messageStatus;
    }
}
