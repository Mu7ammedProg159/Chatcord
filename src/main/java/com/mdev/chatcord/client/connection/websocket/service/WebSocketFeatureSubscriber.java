package com.mdev.chatcord.client.connection.websocket.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdev.chatcord.client.connection.websocket.impl.SubscriptionDefinition;
import com.mdev.chatcord.client.connection.websocket.impl.WebSocketSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompSession;

import java.util.*;
import java.util.function.Consumer;

public abstract class WebSocketFeatureSubscriber implements WebSocketSubscriber {
    public final Logger log = LoggerFactory.getLogger(getClass());
    protected final List<SubscriptionDefinition<?>> subscriptions = new ArrayList<>();

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void subscribe(StompSession session) {
        for (SubscriptionDefinition<?> def : subscriptions) {
            session.subscribe(def.destination, def.frameHandler);
        }
    }

    protected <T> void register(String destination, Class<T> payloadType, Consumer<T> handler) {
        subscriptions.add(new SubscriptionDefinition<>(destination, payloadType, handler));
    }

    public <T> void registerList(String destination, Class<T> itemType, Consumer<List<T>> handler) {
        JavaType type = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, itemType);

        subscriptions.add(new SubscriptionDefinition<>(destination, type, handler));
    }

    public <T> void registerSet(String destination, Class<T> itemType, Consumer<Set<T>> handler) {
        JavaType type = objectMapper.getTypeFactory()
                .constructCollectionType(Set.class, itemType);

        subscriptions.add(new SubscriptionDefinition<>(destination, type, handler));
    }

    public <T> void registerArrayList(String destination, Class<T> itemType, Consumer<ArrayList<T>> handler) {
        JavaType type = objectMapper.getTypeFactory()
                .constructCollectionType(ArrayList.class, itemType);

        subscriptions.add(new SubscriptionDefinition<>(destination, type, handler));
    }

//
//    public <C extends Collection<T>, T> void registerCollection(
//            String destination,
//            Class<C> collectionType, // List.class, Set.class, etc.
//            Class<T> itemType,
//            Consumer<C> handler
//    ) {
//        JavaType javaType = objectMapper.getTypeFactory()
//                .constructCollectionType(collectionType, itemType);
//
//        @SuppressWarnings("Unchecked")
//        SubscriptionDefinition<C> def = new SubscriptionDefinition<>(destination, javaType, handler);
//        subscriptions.add(def);
//    }

    protected <K, V> void registerMap(
            String destination,
            Class<K> keyType,
            Class<V> valueType,
            Consumer<Map<K, V>> handler
    ) {
        JavaType javaType = objectMapper.getTypeFactory()
                .constructMapType(Map.class, keyType, valueType);

        SubscriptionDefinition<Map<K, V>> def = new SubscriptionDefinition<>(destination, javaType, handler);
        subscriptions.add(def);
    }
}
