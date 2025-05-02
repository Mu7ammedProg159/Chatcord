package com.mdev.chatcord.client.service;

import com.mdev.chatcord.client.dto.JwtRequest;
import com.mdev.chatcord.client.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserService {

    private final WebClient webClient = WebClient.create();
    private final JwtRequest jwtRequest;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public UserDTO login(String email, String password){

        String jwtToken = webClient.post()
                .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("email", email, "password", password))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        logger.info("This is the token for the signed user: " + jwtToken);

        jwtRequest.setUserDTO(webClient.get()
                .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/users/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .block());

        return jwtRequest.getUserDTO();
    }
}

