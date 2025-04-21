package com.mdev.messanger.client.dto;

import com.mdev.messanger.client.connection.EMessageStatus;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessageDTO implements Serializable {

    private String username;
    private String message;
    private String profileImageURL;
    private long timestamp;
    private EMessageStatus messageStatus;

    @Override
    public String toString() {
        return "MessageDTO{" +
                "username='" + username + '\'' +
                ", message='" + message + '\'' +
                ", profileImageURL='" + profileImageURL + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", MessageStatus='" + messageStatus + '\'' +
                '}';
    }
}
