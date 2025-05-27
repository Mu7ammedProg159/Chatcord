package com.mdev.chatcord.client.chat.dto;

import com.mdev.chatcord.client.message.dto.MessageDTO;
import com.mdev.chatcord.client.friend.enums.EFriendStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ChatDTO {
    private String chatType;
    private LocalDateTime createdAt;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private String lastMessageSender;
    private List<ChatMemberDTO> chatMembersDto;
    private List<MessageDTO> messages;
    private UnreadStatus unreadStatus;
    private EFriendStatus relationship;

}
