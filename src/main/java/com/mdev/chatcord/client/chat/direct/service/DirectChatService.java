package com.mdev.chatcord.client.chat.direct.service;

import com.mdev.chatcord.client.authentication.dto.HttpRequest;
import com.mdev.chatcord.client.chat.dto.ChatDTO;
import com.mdev.chatcord.client.exception.BusinessException;
import com.mdev.chatcord.client.exception.GlobalWebClientExceptionHandler;
import com.mdev.chatcord.client.friend.dto.ContactPreview;
import com.mdev.chatcord.client.token.dto.TokenFactory;
import com.mdev.chatcord.client.user.service.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class DirectChatService {

    private final User user;
    private final HttpRequest httpRequest;
    private final TokenFactory tokenFactory;

    private final WebClient webClient = WebClient.create();

    public ChatDTO startDirectChatSession(String receiverUUID){
        try{
            return webClient.get()
                    .uri(httpRequest.uriParams(httpRequest.getPrivateUri() + "/private",
                            "receiver", receiverUUID))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenFactory.getAccessToken())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(ChatDTO.class)
                    .block();
        } catch (BusinessException e){
            log.error(e.getMessage());
            throw new BusinessException(e.getErrorCode(), e.getMessage());
        }
    }

}
