package com.mdev.chatcord.client.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatMemberDTO {
    private String username;
    private String tag;
    private String avatarUrl;
    private ChatRoleDTO role; // ADMIN, MOD, MEMBER, etc.
}
