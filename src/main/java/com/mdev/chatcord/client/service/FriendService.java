package com.mdev.chatcord.client.service;

import com.mdev.chatcord.client.dto.FriendDTO;
import com.mdev.chatcord.client.dto.JwtRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendService {

    private final WebClient webClient = WebClient.create();
    private final JwtRequest jwtRequest;

    @Getter
    private String errorMessage = "";

    public FriendDTO addFriend(String username, String tag){
        try{
            return webClient.get()
                    .uri(jwtRequest.getDomain() + jwtRequest.getRequestUri() + "/users/" + username + "/" + tag)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(FriendDTO.class)
                    .block();
        } catch (RuntimeException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<FriendDTO> getAllFriends() {
        try {
            return webClient.get()
                    .uri(jwtRequest.getDomain() + jwtRequest.getRequestUri() + "/users/friend/all")
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(new ParameterizedTypeReference<List<FriendDTO>>() {})
                    .block();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
