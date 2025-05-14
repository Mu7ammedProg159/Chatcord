package com.mdev.chatcord.client.dto;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JwtRequest{

    @Value("${chatcord.server.domain.url}")
    private String domain;
    private String authUri = "/api/auth";
    private String requestUri = "/api/request";
    private UserDTO userDTO;

}
