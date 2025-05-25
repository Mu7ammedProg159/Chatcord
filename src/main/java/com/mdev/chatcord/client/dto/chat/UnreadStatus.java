package com.mdev.chatcord.client.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UnreadStatus {
    private int unreadCount;
    private boolean isMuted;
    private boolean isPinned;
}
