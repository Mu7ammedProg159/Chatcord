package com.mdev.chatcord.client.dto.chat;

import com.mdev.chatcord.client.dto.FriendDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PrivateChatDTO {
    FriendDTO friendDTO;
    ChatDTO chatDTO;
}
