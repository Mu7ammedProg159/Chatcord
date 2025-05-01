package com.mdev.chatcord.client.dto;

import com.mdev.chatcord.client.model.EStatus;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {

    private UUID uuid;
    private String email;
    private String username;
    private String tag;
    private String userSocket;
    private String status;
    private boolean isEmailVerified;

    private String profileImageURL = "/images/default_pfp.png";

    public UserDTO(UUID uuid, String email, String username, String tag, String userSocket, String status, boolean isEmailVerified) {
        this.uuid = uuid;
        this.email = email;
        this.username = username;
        this.tag = tag;
        this.userSocket = userSocket;
        this.status = status;
        this.isEmailVerified = isEmailVerified;
    }

    public UserDTO(String email, String username) {
        this.tag = tag;
        this.username = username;
    }
}
