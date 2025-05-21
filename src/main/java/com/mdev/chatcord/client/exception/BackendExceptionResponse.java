package com.mdev.chatcord.client.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BackendExceptionResponse {
    private String errorCode;
    private String errorMessage;
}