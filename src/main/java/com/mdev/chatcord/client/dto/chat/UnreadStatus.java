package com.mdev.chatcord.client.dto.chat;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class UnreadStatus {
    private int unreadCount;
    private boolean isMuted;
    private boolean isPinned;
}
