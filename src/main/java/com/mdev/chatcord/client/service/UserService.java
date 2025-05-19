package com.mdev.chatcord.client.service;

import com.mdev.chatcord.client.dto.DeviceDto;
import com.mdev.chatcord.client.dto.HttpRequest;
import com.mdev.chatcord.client.dto.UserDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class UserService {

    private final WebClient webClient = WebClient.create();
    private final HttpRequest jwtRequest;
    private final DeviceDto deviceDto;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Getter
    private String errorMessage = "";

    public String register(String email, String password, String username){

        try {
            String message = webClient.post()
                    .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("email", email, "password", password, "username", username))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(String.class)
                    .block();
            return message;

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public String verify(String email, String otp){

        try {
            String message = webClient.post()
                    .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/verify-email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("email", email, "otp", otp))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(String.class)
                    .block();
            return message;

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String resendOtp(String email){

        try {
            String message = webClient.post()
                    .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/resend-otp")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("email", email))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(String.class)
                    .block();
            return message;

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public long canResendOtp(String email){

        try {
            long message = Long.parseLong(Objects.requireNonNull(webClient.post()
                    .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/retry-otp-send")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("email", email))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(String.class)
                    .block()));
            return message;

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void login(String email, String password) {

        try {
            var tokens = webClient.post()
                    .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("User-Agent", deviceDto.getOS())
                    .bodyValue(Map.of("email", email, "password", password, "deviceDto", deviceDto))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                    .block();

            assert tokens != null;
            jwtRequest.setAccessToken(tokens.get(0));
            jwtRequest.setRefreshToken(tokens.get(1), deviceDto.getDEVICE_ID());

        } catch (RuntimeException | IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        logger.info("Access token for the signed user: " + jwtRequest.getAccessToken());
        logger.info("Refresh token for the signed user: " + jwtRequest.getRefreshToken());

        try{
            jwtRequest.setUserDTO(webClient.get()
                    .uri(jwtRequest.getDomain() + jwtRequest.getRequestUri() + "/users/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtRequest.getAccessToken())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(UserDTO.class)
                    .block());

        } catch (RuntimeException e){
            throw new RuntimeException(e.getMessage());
        }
    }
}

