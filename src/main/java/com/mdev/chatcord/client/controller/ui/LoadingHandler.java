package com.mdev.chatcord.client.controller.ui;

import com.mdev.chatcord.client.component.ThrowingRunnable;

public interface LoadingHandler {
    ThrowingRunnable loadOnCall();
    ThrowingRunnable loadOnSuccess();
    ThrowingRunnable loadOnFailure();
}
