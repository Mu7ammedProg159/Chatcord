package com.mdev.chatcord.client.connection.websocket.service;

import com.mdev.chatcord.client.connection.websocket.annotation.WebSocketSubscriber;
import com.mdev.chatcord.client.user.service.User;
import org.reflections.Reflections;
import org.springframework.context.ApplicationEventPublisher;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;

public class SubscriptionRegistrar {

    public static List<com.mdev.chatcord.client.connection.websocket.impl.WebSocketSubscriber> findAll(ApplicationEventPublisher eventPublisher, User user) {
        Reflections reflections = new Reflections("com.mdev.chatcord.client.connection.websocket.subscriptions");

        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(WebSocketSubscriber.class);

        return annotated.stream()
                .filter(com.mdev.chatcord.client.connection.websocket.impl.WebSocketSubscriber.class::isAssignableFrom)
                .map(clazz -> {
                    try {
                        Constructor<?> constructor = clazz.getConstructor(ApplicationEventPublisher.class, User.class);
                        return (com.mdev.chatcord.client.connection.websocket.impl.WebSocketSubscriber) constructor.newInstance(eventPublisher, user);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to instantiate " + clazz.getSimpleName(), e);
                    }
                })
                .toList();
    }
}
