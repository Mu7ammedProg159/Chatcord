package com.mdev.chatcord.client.service;

import com.mdev.chatcord.client.dto.DeviceDto;
import com.mdev.chatcord.client.dto.HttpRequest;
import com.mdev.chatcord.client.dto.Profile;
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
import java.util.UUID;

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
            logger.info(e.getMessage());
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
        List<String> response = null;
        try {
            response = webClient.post()
                    .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("User-Agent", deviceDto.getOS())
                    .bodyValue(Map.of("email", email, "password", password, "deviceDto", deviceDto))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                    .block();

            assert response != null;
            jwtRequest.setAccessToken(response.get(0));

        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        try{
            jwtRequest.setUserDTO(webClient.get()
                    .uri(jwtRequest.getDomain() + jwtRequest.getRequestUri() + "/users/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtRequest.getAccessToken())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(Profile.class)
                    .block());

            if (response.get(1) != null)
                jwtRequest.setRefreshToken(response.get(1), deviceDto.getDEVICE_ID(), String.valueOf(jwtRequest.getUserDTO().getUuid()));

        } catch (RuntimeException | IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public void logout(){
        try {
            String response = webClient.post()
                    .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/logout?deviceId=" + deviceDto.getDEVICE_ID())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtRequest.getAccessToken())
                    .bodyValue(Map.of("DEVICE_ID", deviceDto.getDEVICE_ID()))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(String.class)
                    .block();

            jwtRequest.deleteRefreshKeyFile(deviceDto.getDEVICE_ID(), String.valueOf(jwtRequest.getUserDTO().getUuid()));

            logger.info(response);
        } catch (RuntimeException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}

