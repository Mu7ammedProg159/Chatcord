package com.mdev.chatcord.client.chat.direct.dto;

import com.mdev.chatcord.client.chat.ChatDTO;
import com.mdev.chatcord.client.friend.dto.FriendContactDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PrivateChatDTO {
    FriendContactDTO friendContactDTO;
    ChatDTO chatDTO;
}
