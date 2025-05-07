package com.mdev.chatcord.client.component;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Exception;
}