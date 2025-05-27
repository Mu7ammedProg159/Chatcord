package com.mdev.chatcord.client.common.implementation;

public interface LoadingHandler {
    ThrowingRunnable loadOnCall();
    ThrowingRunnable loadOnSuccess();
    ThrowingRunnable loadOnFailure();
}
