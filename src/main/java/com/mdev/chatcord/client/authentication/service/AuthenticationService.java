package com.mdev.chatcord.client.authentication.service;

import com.mdev.chatcord.client.authentication.dto.AuthenticationResponse;
import com.mdev.chatcord.client.device.dto.DeviceDto;
import com.mdev.chatcord.client.authentication.dto.HttpRequest;
import com.mdev.chatcord.client.exception.BusinessException;
import com.mdev.chatcord.client.exception.GlobalWebClientExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthenticationService {

    private final WebClient webClient = WebClient.create();
    private final HttpRequest jwtRequest;
    private final DeviceDto deviceDto;

    public String register(String email, String password, String username) {

        // We ignore device DTO since it is only a validation phase, Hence, it will be on login not registration.
        try {
            String message = GlobalWebClientExceptionHandler.wrapWithFallback(() ->
                    webClient.post()
                            .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("email", email, "password", password, "username", username))
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                            .bodyToMono(String.class)
                            .block());
            return message;

        } catch (BusinessException e) {
            throw new BusinessException(e.getErrorCode(), e.getMessage());
        }

    }

    public String verify(String email, String otp) {

        try {
            String message = GlobalWebClientExceptionHandler.wrapWithFallback(() ->
                    webClient.post()
                            .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/email/verify")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("email", email, "otp", otp))
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                            .bodyToMono(String.class)
                            .block());
            return message;

        } catch (BusinessException e) {
            log.info(e.getMessage());
            throw new BusinessException(e.getErrorCode(), e.getMessage());
        }
    }

    public String resendOtp(String email) {

        try {
            String message = GlobalWebClientExceptionHandler.wrapWithFallback(() ->
                    webClient.post()
                            .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/otp/resend")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("email", email))
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                            .bodyToMono(String.class)
                            .block());
            return message;

        } catch (BusinessException e) {
            throw new BusinessException(e.getErrorCode(), e.getMessage());
        }
    }

    public long canResendOtp(String email) {

        try {
            long message = Long.parseLong(Objects.requireNonNull(GlobalWebClientExceptionHandler.wrapWithFallback(() ->
                    webClient.post()
                            .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/otp/retry-otp-send")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("email", email))
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                            .bodyToMono(String.class)
                            .block())));
            return message;

        } catch (BusinessException e) {
            throw new BusinessException(e.getErrorCode(), e.getMessage());
        }
    }

    public void login(String email, String password) {
        AuthenticationResponse response = null;
        try {
            response = GlobalWebClientExceptionHandler.wrapWithFallback(() ->
                    webClient.post()
                            .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("email", email, "password", password, "deviceDto", deviceDto))
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                            .bodyToMono(AuthenticationResponse.class)
                            .block());

            jwtRequest.setUserDTO(response.getProfileDetails());
            jwtRequest.setAccessToken(response.getAccessToken());
            jwtRequest.setRefreshToken(response.getRefreshToken(), deviceDto.getDEVICE_ID(), response.getProfileDetails().getUuid());
            jwtRequest.getUserDTO().setEmail(email);

        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw new BusinessException(e.getErrorCode(), e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void logout() {
        try {
            String response = GlobalWebClientExceptionHandler.wrapWithFallback(() ->
                    webClient.post()
                            .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/logout?deviceId=" + deviceDto.getDEVICE_ID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtRequest.getAccessToken())
                            .bodyValue(Map.of("DEVICE_ID", deviceDto.getDEVICE_ID()))
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                            .bodyToMono(String.class)
                            .block());

            jwtRequest.deleteRefreshKeyFile(deviceDto.getDEVICE_ID(), String.valueOf(jwtRequest.getUserDTO().getUuid()));

            log.info(response);
        } catch (BusinessException e) {
            log.info(e.getMessage());
            throw new BusinessException(e.getErrorCode(), e.getMessage());
        }
    }
}

