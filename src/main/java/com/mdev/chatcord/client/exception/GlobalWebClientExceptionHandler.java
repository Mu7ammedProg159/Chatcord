package com.mdev.chatcord.client.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

public class GlobalWebClientExceptionHandler {

    public static Mono<? extends Throwable> handleResponse(ClientResponse response) {
                return response.bodyToMono(BackendExceptionResponse.class)
                        .map(error ->
                                new BusinessException(error.getErrorCode(),
                                        error.getErrorMessage()));
    }
}