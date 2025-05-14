package com.mdev.chatcord.client.dto;

import com.mdev.chatcord.client.enums.EFriendStatus;
import com.mdev.chatcord.client.enums.EUserState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendDTO {
    private String username;
    private String tag;
    private String friendName;
    private String friendTag;
    private String friendPfp;
    private EFriendStatus friendStatus;
    private EUserState friendState;
    private LocalDateTime addedAt;
}
