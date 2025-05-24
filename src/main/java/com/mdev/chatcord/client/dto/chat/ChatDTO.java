package com.mdev.chatcord.client.dto.chat;

import com.mdev.chatcord.client.enums.ChatType;
import com.mdev.chatcord.client.enums.EFriendStatus;
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
    private ChatType chatType;
    private LocalDateTime createdAt;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private String lastMessageSender;
    private List<ChatMemberDTO> chatMembersDto;
    private List<MessageDTO> messages;
    private UnreadStatus unreadStatus;
    private EFriendStatus relationship;

}
