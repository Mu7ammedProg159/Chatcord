package com.mdev.messanger.client.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class UserDTO {

    private String username;
    private String tag;
    private String userSocket;
    private String lastMessage;
    private String profileImageURL = "/images/default_pfp.png";
    private LocalDateTime timestamp;

    public UserDTO(String username, String tag){
        super();
        this.username = username;
        this.tag = tag;
    }

}
