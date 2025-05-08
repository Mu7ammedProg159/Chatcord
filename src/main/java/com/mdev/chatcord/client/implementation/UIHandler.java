package com.mdev.chatcord.client.implementation;

import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.text.Font;

import java.util.Objects;

public interface UIHandler extends UIErrorHandler {
    public default Image createImage(String imagePath){
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
    }
    public default void changeFont(Labeled node, String fontPath){
        node.setFont(Font.loadFont(getClass().getResourceAsStream(fontPath), 12));
    }
    public default void changeFont(Labeled node, String fontPath, int fontSize){
        node.setFont(Font.loadFont(getClass().getResourceAsStream(fontPath), fontSize));
    }
}
