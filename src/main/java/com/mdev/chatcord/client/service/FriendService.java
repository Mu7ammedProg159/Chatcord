package com.mdev.chatcord.client.service;

import com.mdev.chatcord.client.dto.FriendDTO;
import com.mdev.chatcord.client.dto.HttpRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendService {

    private final WebClient webClient = WebClient.create();
    private final HttpRequest jwtRequest;

    @Getter
    private String errorMessage = "";

    public FriendDTO addFriend(String username, String tag){
        try{
            return webClient.get()
                    .uri(jwtRequest.getDomain() + jwtRequest.getFriendUri() + "/add/" + username + "/" + tag)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtRequest.getToken())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(FriendDTO.class)
                    .block();
        } catch (RuntimeException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<FriendContactDTO> getAllFriends() {
        try {
            return webClient.get()
                    .uri(jwtRequest.getDomain() + jwtRequest.getFriendUri() + "/all")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtRequest.getToken())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(new ParameterizedTypeReference<List<FriendContactDTO>>() {})
                    .block();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public FriendContactDTO getFriend(String username, String tag) {
        try {
            return webClient.get()
                    .uri(jwtRequest.getDomain() + jwtRequest.getFriendUri() + "/" + username + "/" + tag)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtRequest.getToken())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(FriendContactDTO.class)
                    .block();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<FriendContactDTO> getAllPendingFriends() {
        try {
            return webClient.get()
                    .uri(jwtRequest.getDomain() + jwtRequest.getFriendUri() + "/pending/all")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtRequest.getToken())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(new ParameterizedTypeReference<List<FriendContactDTO>>() {})
                    .block();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
