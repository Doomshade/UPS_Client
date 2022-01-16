package jsmahy.ups_client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.out.play.PacketPlayOutMessage;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    private static GameController instance = null;

    @FXML
    public TextField chat;

    @FXML
    public TextArea opponentChat;

    @FXML
    public Button chatSend;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
    }

    public static GameController getInstance() {
        return instance;
    }

    public void sendMessage(ActionEvent actionEvent) {
        NetworkManager.getInstance().sendPacket(new PacketPlayOutMessage(chat.getText()));
        chat.clear();
    }

    public void sendMessageEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            sendMessage(null);
        }
    }
}
