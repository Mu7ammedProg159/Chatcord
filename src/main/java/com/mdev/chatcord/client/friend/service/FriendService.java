package com.mdev.chatcord.client.friend.service;

import com.mdev.chatcord.client.authentication.dto.HttpRequest;
import com.mdev.chatcord.client.chat.direct.dto.PrivateChatDTO;
import com.mdev.chatcord.client.exception.BusinessException;
import com.mdev.chatcord.client.exception.GlobalWebClientExceptionHandler;
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

    public PrivateChatDTO addFriend(String username, String tag){
        try{
            return webClient.get()
                    .uri(jwtRequest.getDomain() + jwtRequest.getFriendUri() + "/add/" + username + "/" + tag)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtRequest.getAccessToken())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(PrivateChatDTO.class)
                    .block();
        } catch (BusinessException e){
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<PrivateChatDTO> getAllFriends() {
        try {
            return webClient.get()
                    .uri(jwtRequest.getDomain() + jwtRequest.getFriendUri() + "/all")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtRequest.getAccessToken())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(new ParameterizedTypeReference<List<PrivateChatDTO>>() {})
                    .block();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public PrivateChatDTO getFriend(String username, String tag) {
        try {
            return webClient.get()
                    .uri(jwtRequest.getDomain() + jwtRequest.getFriendUri() + "/" + username + "/" + tag)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtRequest.getAccessToken())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(PrivateChatDTO.class)
                    .block();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<PrivateChatDTO> getAllPendingFriends() {
        try {
            return webClient.get()
                    .uri(jwtRequest.getDomain() + jwtRequest.getFriendUri() + "/pending/all")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtRequest.getAccessToken())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(new ParameterizedTypeReference<List<PrivateChatDTO>>() {})
                    .block();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
