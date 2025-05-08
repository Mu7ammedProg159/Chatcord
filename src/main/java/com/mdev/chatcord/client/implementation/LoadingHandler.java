package com.mdev.chatcord.client.implementation;

public interface LoadingHandler {
    ThrowingRunnable loadOnCall();
    ThrowingRunnable loadOnSuccess();
    ThrowingRunnable loadOnFailure();
}
