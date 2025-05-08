package com.mdev.chatcord.client.implementation;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws Exception;
}