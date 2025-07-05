package com.mdev.chatcord.client.friend.dto;

import javafx.scene.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Loader<T> {
    private Node node;
    private T controller;
}
