package jsmahy.ups_client.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import jsmahy.ups_client.SceneManager;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.ProtocolState;
import jsmahy.ups_client.net.out.logged_in.PacketLoggedInOutJoinQueue;
import jsmahy.ups_client.net.out.queue.PacketQueueOutLeaveQueue;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class PlaySceneController implements Initializable {
	private static PlaySceneController instance = null;
	@FXML
	public Button zpetButton;
	@FXML
	public Button startButton;
	@FXML
	public Button leaveQueueButton;
	@FXML
	public ChoiceBox<String> gameTypeChoiceBox;

	@FXML
	public VBox nodes;
	@FXML
	public ProgressIndicator indicator;

	public void goBack(final ActionEvent actionEvent) {
		leaveQueue(actionEvent);
		SceneManager.changeScene(SceneManager.Scenes.MAIN_MENU);
	}

	public void leaveQueue(final ActionEvent actionEvent) {
		final NetworkManager nm = NetworkManager.getInstance();
		if (nm.getState() == ProtocolState.QUEUE) {
			nm.sendPacket(new PacketQueueOutLeaveQueue(), null, null, nodes, indicator);
		}
	}

	public void joinQueue(final ActionEvent actionEvent) {
		NetworkManager.getInstance().sendPacket(new PacketLoggedInOutJoinQueue(), null, null, nodes, indicator);
	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		NetworkManager.getInstance().addChangedStateListener(x -> updateButtons());
		updateButtons();
		final ObservableList<String> list = gameTypeChoiceBox.getItems();
		list.add("BLITZ");
		list.add("RAPID");
		list.add("CLASSICAL");
	}

	public void updateButtons() {
		final boolean inQueue = NetworkManager.getInstance().getState() == ProtocolState.QUEUE;
		startButton.setDisable(inQueue);
		leaveQueueButton.setDisable(!inQueue);
	}
}
