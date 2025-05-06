package com.mdev.chatcord.client.controller.ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

@Component
public class LoadingController implements UIErrorHandler {

    @FXML private HBox loadingDots;
    @FXML private Circle dot1, dot2, dot3;

    private Timeline loadingAnimation;

    public void initialize() {

        setLoadingVisibility();

        startDotLoadingAnimation(dot1, dot2, dot3);
    }

    private void setLoadingVisibility() {
        loadingDots.setDisable(true);
        loadingDots.setManaged(false);
        loadingDots.setVisible(false);
    }

    public void startDotLoadingAnimation(Circle dot1, Circle dot2, Circle dot3) {
        Timeline timeline = new Timeline();

        Duration cycle = Duration.seconds(1.2); // total cycle time
        double interval = 0.4;

        timeline.getKeyFrames().addAll(
                // Dot 1 brightens
                new KeyFrame(Duration.seconds(0 * interval),
                        new KeyValue(dot1.opacityProperty(), 1),
                        new KeyValue(dot2.opacityProperty(), 0.3),
                        new KeyValue(dot3.opacityProperty(), 0.3)
                ),
                // Dot 2 brightens
                new KeyFrame(Duration.seconds(1 * interval),
                        new KeyValue(dot1.opacityProperty(), 0.3),
                        new KeyValue(dot2.opacityProperty(), 1),
                        new KeyValue(dot3.opacityProperty(), 0.3)
                ),
                // Dot 3 brightens
                new KeyFrame(Duration.seconds(2 * interval),
                        new KeyValue(dot1.opacityProperty(), 0.3),
                        new KeyValue(dot2.opacityProperty(), 0.3),
                        new KeyValue(dot3.opacityProperty(), 1)
                ),
                // Smooth return to Dot 1
                new KeyFrame(Duration.seconds(3 * interval),
                        new KeyValue(dot1.opacityProperty(), 1),
                        new KeyValue(dot2.opacityProperty(), 0.3),
                        new KeyValue(dot3.opacityProperty(), 0.3)
                )
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setAutoReverse(false);
        timeline.play();
    }
}
