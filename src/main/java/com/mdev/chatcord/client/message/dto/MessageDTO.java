package com.mdev.chatcord.client.message.dto;

import com.mdev.chatcord.client.chat.dto.ChatMemberDTO;
import com.mdev.chatcord.client.chat.enums.ChatType;
import com.mdev.chatcord.client.message.enums.EMessageStatus;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MessageDTO implements Serializable {

    private ChatType chatType;
    private String content;
    private ChatMemberDTO sender; // uuid
    private ChatMemberDTO receiver; // Can be username#tag or guildId << this is currently is the group.
    private LocalDateTime sentAt;
    private boolean isEdited;
    private EMessageStatus messageStatus;
}
