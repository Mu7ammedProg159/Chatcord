package com.mdev.chatcord.client;

import com.mdev.chatcord.client.dto.DeviceDto;
import com.mdev.chatcord.client.dto.HttpRequest;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

public class ChatcordApplication extends Application {

	private static ConfigurableApplicationContext applicationContext;

	@Autowired
	private DeviceDto deviceDto;

	@Autowired
	private HttpRequest httpRequest;

	@Override
	public void init() {
		applicationContext = new SpringApplicationBuilder(SpringBootApp.class).run();
	}

	@Override
	public void start(Stage stage) throws Exception {
		applicationContext.publishEvent(new StageReadyEvent(stage));
	}

	@Override
	public void stop() throws Exception {
		applicationContext.close();
		Platform.exit();
	}

	public static class StageReadyEvent extends ApplicationEvent {
		public StageReadyEvent(Stage stage) {
			super(stage);
		}

		public Stage getStage() {
			return ((Stage) getSource());
		}
	}
}
