package jsmahy.ups_client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import jsmahy.ups_client.SceneManager;
import jsmahy.ups_client.net.NetworkManager;

/**
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class MainMenuController {
	@FXML
	public Button playButton;
	@FXML
	public Button exitButton;

	@FXML
	public Button logoutButton;

	public void showPlayScene(final ActionEvent actionEvent) {
		SceneManager.changeScene(SceneManager.Scenes.PLAY_SCENE);
	}

	public void exit(final ActionEvent actionEvent) {
		Platform.exit();
		System.exit(0);
	}

	public void logout(final ActionEvent actionEvent) {
		NetworkManager.getInstance().disconnect(null, null, null, true);
	}
}
