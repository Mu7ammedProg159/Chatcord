package com.mdev.chatcord.client.chat.direct.dto;

import com.mdev.chatcord.client.chat.dto.ChatDTO;
import com.mdev.chatcord.client.friend.dto.ContactPreview;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PrivateChatDTO {
    ContactPreview contactPreview;
    ChatDTO chatDTO;
}
