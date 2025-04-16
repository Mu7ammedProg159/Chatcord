package com.mdev.messanger.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long uid;

    private String username;
    private String Password;
    private String email;
    private String tag;
    private String messagesHistory;
    private EStatus Status;
}
