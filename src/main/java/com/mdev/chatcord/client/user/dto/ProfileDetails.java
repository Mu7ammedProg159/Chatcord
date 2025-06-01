package com.mdev.chatcord.client.user.dto;

import com.mdev.chatcord.client.authentication.dto.AuthenticationResponse;
import lombok.*;
import org.springframework.stereotype.Component;

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
    private String about;
    private String quote;

    public ProfileDetails (String username){
        this.username = username;
    }


}
