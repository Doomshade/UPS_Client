package jsmahy.ups_client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.listener.impl.Client;
import jsmahy.ups_client.net.out.play.PacketPlayOutMessage;
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
	public Button chatSend;

	@FXML
	public BorderPane rootPane;

	public DraggableGrid draggableGrid = null;

	public static GameController getInstance() {
		return instance;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		instance = this;
		this.draggableGrid = new DraggableGrid(Client.getClient().getChessGame().getChessboard());
		final GridPane gridPane = draggableGrid.getGridPane();
		gridPane.setAlignment(Pos.CENTER);
		rootPane.setCenter(gridPane);
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

}
