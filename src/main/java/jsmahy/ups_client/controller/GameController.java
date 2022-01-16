package jsmahy.ups_client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.listener.impl.Client;
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

    public static GameController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
    }

    public void sendMessage(ActionEvent actionEvent) {
        chat.setText(chat.getText().substring(0, Math.min(chat.getText().length(), 50)));
        NetworkManager.getInstance().sendPacket(new PacketPlayOutMessage(chat.getText()));
        opponentChat.appendText(String.format("[%s]:\t%s%n", Client.getClient().getPlayer().getName(), chat.getText()));
        chat.clear();
    }

}
