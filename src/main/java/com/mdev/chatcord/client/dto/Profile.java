package com.mdev.chatcord.client.dto;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Profile {

    private UUID uuid;
    private String email;
    private String username;
    private String tag;
    private String status;
    private String pfpUrl = "/images/default_pfp.png";
    private String about;
    private String quote;

}
