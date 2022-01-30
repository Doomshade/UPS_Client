package jsmahy.ups_client;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public final class SceneManager {
	private static final Logger L = LogManager.getLogger(SceneManager.class);

	private SceneManager() {
	}

	public static synchronized void changeScene(Scenes scenes) {
		Platform.runLater(() -> {
			FXMLLoader fxmlLoader =
					new FXMLLoader(Main.class.getResource(String.format("/fxml/%s.fxml", scenes.scene)));
			Scene scene;
			try {
				scene = new Scene(fxmlLoader.load());
			} catch (IOException e) {
				L.fatal("Failed to load a scene!", e);
				Platform.exit();
				System.exit(0);
				return;
			}
			Main.getStage().setScene(scene);
		});
	}

	public enum Scenes {
		SERVER_CONNECTION("server-connection-view"),
		MAIN_MENU("main-menu-view"),
		PLAY_SCENE("play-view"),
		GAME_SCENE("game-view");

		private final String scene;

		Scenes(final String scene) {
			this.scene = scene;
		}
	}
}
