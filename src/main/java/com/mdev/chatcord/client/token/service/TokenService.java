package com.mdev.chatcord.client.token.service;

import com.mdev.chatcord.client.authentication.dto.HttpRequest;
import com.mdev.chatcord.client.device.dto.DeviceDto;
import com.mdev.chatcord.client.exception.GlobalWebClientExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final HttpRequest httpRequest;
    private final WebClient webClient = WebClient.create();
    private final DeviceDto deviceDto;

    public String refreshAccessToken(String refreshToken){
        return GlobalWebClientExceptionHandler.wrapWithFallback(() ->
                webClient.put()
                        .uri(httpRequest.uriParams(httpRequest.getAuthUri() + "/refresh-access", "deviceId", deviceDto.getDEVICE_ID()))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + refreshToken)
                        .retrieve()
                        .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                        .bodyToMono(String.class)
                        .block());
    }
}
