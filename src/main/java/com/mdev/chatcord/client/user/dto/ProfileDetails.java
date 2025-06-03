package com.mdev.chatcord.client.user.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProfileDetails {

    private String uuid;
    private String email;
    private String username;
    private String tag;
    private String status;
    private String pfpUrl;
    private String avatarColor;
    private String about;
    private String quote;

    public ProfileDetails (String username){
        this.username = username;
    }


}
