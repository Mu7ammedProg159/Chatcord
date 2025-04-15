package com.mdev.messanger.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class UserSession {
    private Long uid;
    @Setter
    private String username;

    public void clear(){
        uid = null;
        username = null;
    }

    public boolean isLoggedIn(){
        return username != null;
    }
}
