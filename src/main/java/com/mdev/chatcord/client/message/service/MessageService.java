package com.mdev.chatcord.client.message.service;

import com.mdev.chatcord.client.authentication.dto.HttpRequest;
import com.mdev.chatcord.client.exception.BusinessException;
import com.mdev.chatcord.client.exception.GlobalWebClientExceptionHandler;
import com.mdev.chatcord.client.friend.dto.ContactPreview;
import com.mdev.chatcord.client.message.dto.MessageDTO;
import com.mdev.chatcord.client.message.enums.EMessageStatus;
import com.mdev.chatcord.client.token.dto.TokenFactory;
import com.mdev.chatcord.client.user.service.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final WebClient webClient = WebClient.create();
    private final User user;
    private final HttpRequest httpRequest;
    private final TokenFactory tokenFactory;

    public MessageDTO changeMessageStatus(MessageDTO message, EMessageStatus messageStatus) {
        try{
            return webClient.put()
                    .uri(httpRequest.getMessageStateUri() + "/" + messageStatus.name().toLowerCase())
                    .bodyValue(Map.of("message", message))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenFactory.getAccessToken())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(MessageDTO.class)
                    .block();
        } catch (BusinessException e){
            log.error(e.getMessage());
            throw new BusinessException(e.getErrorCode(), e.getMessage());
        }

    }

}
