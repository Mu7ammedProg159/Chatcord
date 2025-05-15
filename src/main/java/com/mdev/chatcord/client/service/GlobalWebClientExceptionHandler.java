package com.mdev.chatcord.client.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

public class GlobalWebClientExceptionHandler {

    public static Mono<? extends Throwable> handleResponse(ClientResponse response) {
        HttpStatus status = (HttpStatus) response.statusCode();

        return response.bodyToMono(String.class)
            .defaultIfEmpty("No error message provided.")
            .flatMap(errorBody -> {
                String message = switch (status) {
                    case UNAUTHORIZED, FORBIDDEN, NOT_FOUND, LOCKED, TOO_MANY_REQUESTS, CONFLICT, BAD_REQUEST, INTERNAL_SERVER_ERROR, ALREADY_REPORTED -> errorBody;
                    default -> "Error (" + status.value() + "): " + errorBody;
                };
                return Mono.error(new RuntimeException(message));
            });
    }
}