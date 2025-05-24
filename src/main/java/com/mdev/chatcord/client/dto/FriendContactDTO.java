package com.mdev.chatcord.client.dto;

import com.mdev.chatcord.client.enums.EFriendStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FriendContactDTO {
    private String name;
    private String tag;
    private String profilePictureURL;
    private String lastMessageSent;
    private LocalDateTime LastMessageSendDate;
    private EFriendStatus friendStatus;
}
