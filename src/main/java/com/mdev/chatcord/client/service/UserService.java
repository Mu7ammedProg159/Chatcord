package com.mdev.chatcord.client.service;

import com.mdev.chatcord.client.dto.JwtRequest;
import com.mdev.chatcord.client.dto.UserDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class UserService {

    private final WebClient webClient = WebClient.create();
    private final JwtRequest jwtRequest;

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

    public Object login(String email, String password) {

        String jwtToken;
        try {
            jwtToken = webClient.post()
                    .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("email", email, "password", password))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(String.class)
                    .block();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }

        logger.info("This is the token for the signed user: " + jwtToken);

        try{
            jwtRequest.setUserDTO(webClient.get()
                    .uri(jwtRequest.getDomain() + jwtRequest.getAuthUri() + "/users/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, GlobalWebClientExceptionHandler::handleResponse)
                    .bodyToMono(UserDTO.class)
                    .block());

            return jwtRequest.getUserDTO();

        } catch (RuntimeException e){
            throw new RuntimeException(e.getMessage());
        }
    }
}

