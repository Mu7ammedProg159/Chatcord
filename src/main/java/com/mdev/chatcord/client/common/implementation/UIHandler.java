package com.mdev.chatcord.client.common.implementation;

import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
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

    public default void applyTint(ImageView view, Color color) {
        if (view.getImage() == null) return;

        double width = view.getImage().getWidth();
        double height = view.getImage().getHeight();

        ColorInput colorInput = new ColorInput(0, 0, width, height, color);
        Blend blend = new Blend(BlendMode.SRC_ATOP, null, colorInput);
        view.setEffect(blend);
    }

    public default void clearTint(ImageView view) {
        view.setEffect(null);
    }

    public default void bindImageStates(Color normal, Color hover, Color pressed, Color selected, ToggleButton... toggles) {
        for (ToggleButton toggle : toggles) {
            Node graphic = toggle.getGraphic();
            if (!(graphic instanceof ImageView image)) continue;

            // Default state
            applyTint(image, toggle.isSelected() ? selected : normal);

            toggle.hoverProperty().addListener((obs, oldVal, isHovering) -> {
                if (!toggle.isPressed() && !toggle.isSelected()) {
                    applyTint(image, isHovering ? hover : normal);
                }
            });

            toggle.pressedProperty().addListener((obs, oldVal, isPressed) -> {
                if (!toggle.isSelected()) {
                    applyTint(image, isPressed ? pressed : (toggle.isHover() ? hover : normal));
                }
            });

            toggle.selectedProperty().addListener((obs, oldVal, isSelected) -> {
                applyTint(image, isSelected ? selected : (toggle.isHover() ? hover : normal));
            });
        }
    }
}
