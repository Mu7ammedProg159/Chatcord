package com.mdev.chatcord.client.friend.dto;

import com.mdev.chatcord.client.friend.enums.EFriendStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FriendContactDTO {
    private String name;
    private String tag;
    private String profilePictureURL;
    private EFriendStatus friendStatus;

    public FriendContactDTO(String name) {
        this.name = name;
    }
}
