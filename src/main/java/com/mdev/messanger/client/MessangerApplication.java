package com.mdev.messanger.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

public class MessangerApplication extends Application {

	@Setter
	@Getter
	private static ConfigurableApplicationContext applicationContext;

	@Override
	public void init() {
		applicationContext = new SpringApplicationBuilder(SpringBootApp.class)
				.web(WebApplicationType.SERVLET)
				.run();
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
