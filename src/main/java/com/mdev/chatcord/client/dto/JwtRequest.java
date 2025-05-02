package com.mdev.chatcord.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JwtRequest{
    private String domain = "http://localhost:8080";
    private String authUri = "/api/auth";
    private UserDTO userDTO;
}
