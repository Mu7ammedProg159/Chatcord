package com.mdev.chatcord.client.service;

import com.mdev.chatcord.client.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserService {

    private final WebClient webClient = WebClient.create();
    private final String domain = "http://localhost:8080";
    private final String authUri = "/api/auth";

    public UserDTO login(String email, String password){

        Mono<String> token = webClient.post()
                .uri(domain + authUri + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("email", email, "password", password))
                .retrieve()
                .bodyToMono(String.class);

    }




}
