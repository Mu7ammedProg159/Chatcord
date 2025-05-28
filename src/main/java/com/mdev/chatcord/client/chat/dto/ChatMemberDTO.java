package com.mdev.chatcord.client.chat.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ChatMemberDTO {
    private String username;
    private String tag;
    private String avatarUrl;
    private ChatRoleDTO role; // ADMIN, MOD, MEMBER, etc.

    public ChatMemberDTO(String username){
        this.username = username;
    }
}
