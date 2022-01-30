package jsmahy.ups_client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.listener.impl.Client;
import jsmahy.ups_client.net.out.play.PacketPlayOutMessage;
import jsmahy.ups_client.net.out.play.PacketPlayOutResign;
import jsmahy.ups_client.util.AlertBuilder;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

	private static final int MAX_MESSAGE_LENGTH = 50;
	private static GameController instance = null;

	@FXML
	public TextField chat;
	@FXML
	public TextArea opponentChat;
	@FXML
	public BorderPane rootPane;
	@FXML
	public Button resign;

	public DraggableGrid draggableGrid = null;
	@FXML
	public Label opponent;

	@FXML
	public Label player;

	public static GameController getInstance() {
		return instance;
	}

	public void appendMessage(String message) {
		Platform.runLater(() -> opponentChat.appendText(message.concat("\n")));
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		instance = this;
		final Client client = Client.getClient();
		this.draggableGrid = new DraggableGrid(client.getChessGame().getChessboard());
		this.player.setText(client.getPlayer().getName());
		final GridPane gridPane = draggableGrid.getGridPane();
		rootPane.setCenter(gridPane);
		gridPane.setAlignment(Pos.CENTER);
	}

	public void sendMessage(ActionEvent actionEvent) {
		final String text = chat.getText();

		// skip empty messages
		if (text.length() == 0) {
			return;
		}

		// warn about the message length
		if (text.length() > MAX_MESSAGE_LENGTH) {
			new AlertBuilder(Alert.AlertType.WARNING)
					.title("Invalid message")
					.header("Message length")
					.content(String.format("Message is too long! Max length is %d (%d)", MAX_MESSAGE_LENGTH,
							text.length()))
					.build().show();
			return;
		}

		// the message is okay, send the message packet
		NetworkManager.getInstance().sendPacket(new PacketPlayOutMessage(text));
		opponentChat.appendText(String.format("[%s]:\t%s%n", Client.getClient().getPlayer().getName(),
				text));
		chat.clear();
	}

	public void resign(final ActionEvent actionEvent) {
		NetworkManager.getInstance().sendPacket(new PacketPlayOutResign());
	}
}
