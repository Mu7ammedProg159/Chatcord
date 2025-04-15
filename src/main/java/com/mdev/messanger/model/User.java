package com.mdev.messanger.model;

import jakarta.persistence.*;
import lombok.*;

@Entity

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long uid;

    private String username;
    private String Password;
    private String messagesHistory;
    private EStatus Status;

    public Long getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return Password;
    }

    public String getMessagesHistory() {
        return messagesHistory;
    }

    public EStatus getStatus() {
        return Status;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setMessagesHistory(String messagesHistory) {
        this.messagesHistory = messagesHistory;
    }

    public void setStatus(EStatus status) {
        Status = status;
    }
}
