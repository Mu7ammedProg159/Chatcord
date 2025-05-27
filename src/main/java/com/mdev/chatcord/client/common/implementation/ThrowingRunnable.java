package com.mdev.chatcord.client.common.implementation;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Exception;
}