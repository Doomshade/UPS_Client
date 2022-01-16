package jsmahy.ups_client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.listener.impl.Client;
import jsmahy.ups_client.net.out.just_connected.PacketJustConnectedOutHello;
import jsmahy.ups_client.util.AlertBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class ServerConnectionController implements Initializable {
    private static final Logger L = LogManager.getLogger(ServerConnectionController.class);
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
        if (!isValidTF(ipTF) || !isValidTF(portTF)) {
            sendInvalidInputAlert("Input cannot be empty!", "Invalid input");
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
            setProgress(true);
            Client.setLoginName(nameTF.getText());
            if (ipTF.getText().equalsIgnoreCase("null")) {
                NM.setupIO(System.in, null);
            } else {
                NM.setup(ipTF.getText(), port,
                        () -> {
                            setProgress(false);
                            L.error("Failed to join to the server!");
                            sendInvalidInputAlert("Server does not exist!", "Invalid input");
                        },
                        () -> {
                            L.info("Successfully connected to the server");
                            NM.sendPacket(new PacketJustConnectedOutHello(nameTF.getText()), null, null, form, progress);
                        });
            }
        } catch (Exception e) {
            setProgress(false);
            L.error("Failed to join to the server!", e);
            sendInvalidInputAlert("Already connected to a server!", "Invalid input");
        }
    }

    private void setProgress(boolean progress) {
        this.progress.setProgress(-1);
        this.progress.setDisable(!progress);
        this.progress.setVisible(progress);

        this.form.setDisable(progress);
    }

    private void sendInvalidInputAlert(String content, String title) {
        new AlertBuilder(Alert.AlertType.ERROR)
                .content(content)
                .title(title)
                .build()
                .show();
    }

    private boolean isValidTF(TextField tf) {
        return !tf.getText().trim().isEmpty();
    }

    public void errorUsernameExists() {
        setProgress(false);
        sendInvalidInputAlert("A user with that name already exists! Please choose a different name", "Invalid " +
                "username");
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        instance = this;
    }
}