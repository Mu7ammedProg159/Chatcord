package com.mdev.chatcord.client.authentication.service;

import com.mdev.chatcord.client.authentication.dto.AuthenticationResponse;
import com.mdev.chatcord.client.connection.websocket.SocketClientHolder;
import com.mdev.chatcord.client.device.dto.DeviceDto;
import com.mdev.chatcord.client.authentication.dto.HttpRequest;
import com.mdev.chatcord.client.exception.BusinessException;
import com.mdev.chatcord.client.exception.GlobalWebClientExceptionHandler;
import com.mdev.chatcord.client.token.dto.TokenFactory;
import com.mdev.chatcord.client.user.dto.ProfileDetails;
import com.mdev.chatcord.client.user.enums.EUserState;
import com.mdev.chatcord.client.user.service.User;
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
    private final TokenFactory tokenFactory;
    private final DeviceDto deviceDto;
    private final User userProfile;

    public String register(String email, String password, String username) {

        // We ignore device DTO since it is only a validation phase, Hence, it will be on login not registration.
        try {
            return GlobalWebClientExceptionHandler.wrapWithFallback(() ->
                    webClient.post()
                            .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("email", email, "password", password, "username", username))
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                            .bodyToMono(String.class)
                            .block());

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

            ProfileDetails profileDetails = response.getProfileDetails();
            userProfile.build(profileDetails, email);

            tokenFactory.setAccessToken(response.getAccessToken());
            tokenFactory.setRefreshToken(response.getRefreshToken(), deviceDto.getDEVICE_ID(), response.getProfileDetails().getUuid());

            // Form Real-time connection using Websockets.
            SocketClientHolder.init(response.getAccessToken(), userProfile);
            //SocketClientHolder.getInstance().sendStatus(EUserState.ONLINE);

        } catch (BusinessException e) {
            log.error(e.getMessage());
            throw new BusinessException(e.getErrorCode(), e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String verify(String email, String otp) {

        try {
            return GlobalWebClientExceptionHandler.wrapWithFallback(() ->
                    webClient.post()
                            .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/email/verify")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("email", email, "otp", otp))
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                            .bodyToMono(String.class)
                            .block());

        } catch (BusinessException e) {
            log.info(e.getMessage());
            throw new BusinessException(e.getErrorCode(), e.getMessage());
        }
    }

    public String resendOtp(String email) {

        try {
            return GlobalWebClientExceptionHandler.wrapWithFallback(() ->
                    webClient.post()
                            .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/otp/resend")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("email", email))
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                            .bodyToMono(String.class)
                            .block());

        } catch (BusinessException e) {
            throw new BusinessException(e.getErrorCode(), e.getMessage());
        }
    }

    public long canResendOtp(String email) {

        try {
            return Long.parseLong(Objects.requireNonNull(GlobalWebClientExceptionHandler.wrapWithFallback(() ->
                    webClient.post()
                            .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/otp/retry-otp-send")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of("email", email))
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                            .bodyToMono(String.class)
                            .block())));

        } catch (BusinessException e) {
            throw new BusinessException(e.getErrorCode(), e.getMessage());
        }
    }

    public void logout() {
        try {
            String response = GlobalWebClientExceptionHandler.wrapWithFallback(() ->
                    webClient.post()
                            .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/logout?deviceId=" + deviceDto.getDEVICE_ID())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenFactory
                                    .retrieveRefreshToken(userProfile.getUuid(), deviceDto.getDEVICE_ID()))
                            .bodyValue(Map.of("DEVICE_ID", deviceDto.getDEVICE_ID()))
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                            .bodyToMono(String.class)
                            .block());

            tokenFactory.deleteRefreshKeyFile(deviceDto.getDEVICE_ID(), String.valueOf(userProfile.getUuid()));
            SocketClientHolder.getInstance().sendStatus(EUserState.OFFLINE);

            log.info(response);
        } catch (BusinessException e) {
            log.info(e.getMessage());
            throw new BusinessException(e.getErrorCode(), e.getMessage());
        }
    }
}

