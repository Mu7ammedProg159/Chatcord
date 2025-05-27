package com.mdev.chatcord.client.common.controller;

import com.mdev.chatcord.client.common.implementation.ThrowingRunnable;
import com.mdev.chatcord.client.common.implementation.UIErrorHandler;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class LoadingController implements UIErrorHandler {

    @FXML private HBox loadingDots;
    @FXML private Circle dot1, dot2, dot3;

    private Timeline loadingAnimation;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @FXML
    public void initialize() {
        setLoadingVisibility(false);
        startDotLoadingAnimation(dot1, dot2, dot3);
    }

    private void setLoadingVisibility(boolean b) {
        loadingDots.setDisable(!b);
        loadingDots.setManaged(b);
        loadingDots.setVisible(b);
    }

    //@FXML
    public void onLoad(ThrowingRunnable onLoad, ThrowingRunnable onSucceeded, ThrowingRunnable onFailed) throws RuntimeException{

        setLoadingVisibility(true);

        // Simulate or call actual login
        loadingAnimation.play();
        Task<Void> loginTask = new Task<>() {
            @Override
            protected Void call() throws InterruptedException {
                if (onLoad != null){
                    try {
                        onLoad.run();
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
                return null;
            }

            @Override
            protected void succeeded() {
                    try {
                        onSucceeded.run();
                    } catch (Exception e) {
                        logger.error(e.getMessage()); //Use this in production. It makes debugging harder.
                        //throw new RuntimeException(e);
                    }
                    loadingAnimation.stop();
                    setLoadingVisibility(false);
                    // proceed to next page
                }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    Throwable ex = getException();
                    logger.error(ex.getMessage()); //Use this in production. It makes debugging harder.
                    //throw new RuntimeException(ex);
                });
                loadingAnimation.stop();
                setLoadingVisibility(false);
            // show error
            }
        };

        new Thread(loginTask).start();
    }

    public void startDotLoadingAnimation(Circle dot1, Circle dot2, Circle dot3) {
        loadingAnimation = new Timeline();

        Duration cycle = Duration.seconds(1.2); // total cycle time
        double interval = 0.4;

        loadingAnimation.getKeyFrames().addAll(
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

        loadingAnimation.setCycleCount(Animation.INDEFINITE);
        loadingAnimation.setAutoReverse(false);
        //loadingAnimation.play();
    }
}
