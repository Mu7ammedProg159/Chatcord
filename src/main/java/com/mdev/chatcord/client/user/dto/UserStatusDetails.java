package com.mdev.chatcord.client.user.dto;

import com.mdev.chatcord.client.user.enums.EUserState;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserStatusDetails {
    private String userUuid;
    private EUserState state;
}
