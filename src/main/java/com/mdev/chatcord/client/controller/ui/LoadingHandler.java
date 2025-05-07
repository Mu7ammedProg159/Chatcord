package com.mdev.chatcord.client.controller.ui;

import com.mdev.chatcord.client.component.SpringFXMLLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

public interface LoadingHandler {
    public default LoadingController loadLoader(SpringFXMLLoader springFXMLLoader) throws IOException {
        FXMLLoader controllerLoader = springFXMLLoader.getLoader("/view/loading-view.fxml");
        return controllerLoader.getController();
    }
}
