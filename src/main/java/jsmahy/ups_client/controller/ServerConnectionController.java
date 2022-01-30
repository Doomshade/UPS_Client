package jsmahy.ups_client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import jsmahy.ups_client.exception.InvalidProtocolStateException;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.listener.impl.Client;
import jsmahy.ups_client.net.out.just_connected.PacketJustConnectedOutHello;
import jsmahy.ups_client.util.AlertBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.AnnotationTypeMismatchException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ServerConnectionController implements Initializable {
	private static final Logger L = LogManager.getLogger(ServerConnectionController.class);
	private static final String ALLOWED_CHARS = "[a-zA-Z\\d]";
	public static String ip = "";
	public static int port = 10000;
	public static String name = "";
	private static ServerConnectionController instance;
	@FXML
	private ProgressIndicator progress;
	@FXML
	private VBox form;
	@FXML
	private Button connectBtn;
	@FXML
	private TextField nameTF;
	@FXML
	private TextField portTF;
	@FXML
	private TextField ipTF;

	public static ServerConnectionController getInstance() {
		return instance;
	}

	public void connect(final ActionEvent actionEvent) {
		if (!isValidTF(ipTF) || !isValidTF(portTF) || !isValidTF(nameTF)) {
			sendInvalidInputAlert("Invalid name/IP/port!", "Invalid input");
			return;
		}
		final int port;

		try {
			port = Integer.parseInt(portTF.getText());
		} catch (NumberFormatException e) {
			sendInvalidInputAlert("Invalid port!", "Invalid input");
			return;
		}

		final NetworkManager NM = NetworkManager.getInstance();
		try {
			ServerConnectionController.name = nameTF.getText();
			ServerConnectionController.ip = ipTF.getText();
			ServerConnectionController.port = port;
			setProgress(true);
			Client.setLoginName(nameTF.getText());
			NM.setup(ipTF.getText(), port,
					e -> {
						String content = "Connection error";
						if (e instanceof UnknownHostException || e instanceof NoRouteToHostException) {
							content = "Unknown host destination";
						} else if (e instanceof ConnectException) {
							content = "Could not connect to the server";
						}
						setProgress(false);
						L.error(content);
						sendInvalidInputAlert(content, "Invalid input");
					},
					() -> {
						L.info("Successfully connected to the server");
						try {
							NM.sendPacket(new PacketJustConnectedOutHello(nameTF.getText()));
						} catch (IllegalStateException | AnnotationTypeMismatchException | InvalidProtocolStateException e) {
							L.error("Failed to send a packet!");
						}
					});
		} catch (IllegalStateException e) {
			setProgress(false);
			L.error("Already connected");
			sendInvalidInputAlert("Already connected to a server!", "Invalid input");
		} catch (Exception e) {
			setProgress(false);
			L.error("Failed to join to the server!");
			sendInvalidInputAlert("An unknown error occurred!", "Unknown exception");
		}
	}

	private void setProgress(boolean progress) {
		L.debug("Setting progress to " + progress);
		this.progress.setProgress(-1);
		this.progress.setDisable(!progress);
		this.progress.setVisible(progress);

		this.form.setDisable(progress);
	}

	private void sendInvalidInputAlert(String content, String title) {
		Platform.runLater(() -> new AlertBuilder(Alert.AlertType.ERROR)
				.content(content)
				.header("Input error")
				.title(title)
				.build()
				.show());
	}

	private boolean isValidTF(TextField tf) {
		final String text = tf.getText().trim();
		return !text.isEmpty() && Pattern.compile(ALLOWED_CHARS).matcher(text).find();
	}

	public void errorUsernameExists() {
		Platform.runLater(() -> {
			setProgress(false);
			sendInvalidInputAlert("A user with that name already exists! Please choose a different name", "Invalid " +
					"username");
		});
	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		instance = this;
	}
}