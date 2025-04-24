package com.mdev.messanger.client.dto;

import com.mdev.messanger.client.model.EStatus;
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
    private EStatus eStatus = EStatus.Offline;

    public UserDTO(String username, String tag){
        super();
        this.username = username;
        this.tag = tag;
    }

    public UserDTO(String tag, String username, EStatus eStatus) {
        this.tag = tag;
        this.username = username;
        this.eStatus = eStatus;
    }
}
