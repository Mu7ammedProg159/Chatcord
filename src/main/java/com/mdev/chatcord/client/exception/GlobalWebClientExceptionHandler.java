package com.mdev.chatcord.client.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.function.Supplier;

public class GlobalWebClientExceptionHandler {

    public static Mono<? extends Throwable> handleResponse(ClientResponse response) {
                return response.bodyToMono(BackendExceptionResponse.class)
                        .map(error -> new BusinessException(error.getErrorCode(), error.getErrorMessage()));
    }

    public static <T> T wrapWithFallback(Supplier<T> webClientCall) {
        try {
            return webClientCall.get();
        } catch (BusinessException ex) {
            throw ex; // Already structured
        } catch (WebClientRequestException ex) {
            throw new BusinessException("9002", "Server currently on maintenance. PLease try later.");
        } catch (Exception ex) {
            throw new BusinessException("0000", "Unexpected error: " + ex.getMessage());
        }
    }

}