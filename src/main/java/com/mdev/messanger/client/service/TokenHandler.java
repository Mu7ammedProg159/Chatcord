package com.mdev.messanger.client.service;

import org.springframework.stereotype.Component;

@Component
public class TokenHandler {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void clear(){
        this.token = null;
    }

}