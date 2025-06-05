package com.mdev.chatcord.client.friend.service;

import com.mdev.chatcord.client.authentication.dto.HttpRequest;
import com.mdev.chatcord.client.chat.direct.dto.PrivateChatDTO;
import com.mdev.chatcord.client.exception.BusinessException;
import com.mdev.chatcord.client.exception.GlobalWebClientExceptionHandler;
import com.mdev.chatcord.client.friend.dto.ContactPreview;
import com.mdev.chatcord.client.token.dto.TokenFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class FriendService {

    private final WebClient webClient = WebClient.create();
    private final TokenFactory tokenFactory;
    private final HttpRequest httpRequest;

    @Getter
    private String errorMessage = "";

    public ContactPreview addFriend(String username, String tag){
        try{

            return webClient.get()
                    .uri(httpRequest.uriParams(httpRequest.getFriendUri() + "/friend/add",
                            "username", username, "tag", tag))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenFactory.getAccessToken())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(ContactPreview.class)
                    .block();
        } catch (BusinessException e){
            log.error(e.getMessage());
            throw new BusinessException(e.getErrorCode(), e.getMessage());
        }
    }

    public List<ContactPreview> getAllFriends() {
        try {
            log.info(tokenFactory.getAccessToken());
            return webClient.get()
                    .uri(httpRequest.getDomain() + httpRequest.getFriendUri() + "/all")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenFactory.getAccessToken())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(new ParameterizedTypeReference<List<ContactPreview>>() {})
                    .block();
        } catch (BusinessException e) {
            throw new BusinessException(e.getErrorCode(), e.getMessage());
        }
    }

    public ContactPreview getFriend(String username, String tag) {
        try {
            String uri = UriComponentsBuilder
                    .newInstance()
                    .scheme("http")
                    .host(httpRequest.getDomain())
                    .path(httpRequest.getFriendUri() + "/friend")
                    .queryParam("username", username)
                    .queryParam("tag", tag)
                    .build()
                    .toUriString();

            return webClient.get()
                    .uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenFactory.getAccessToken())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(ContactPreview.class)
                    .block();
        } catch (BusinessException e) {
            throw new BusinessException(e.getErrorCode(), e.getMessage());
        }
    }

}
