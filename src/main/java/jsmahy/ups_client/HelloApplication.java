package jsmahy.ups_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.Player;
import jsmahy.ups_client.net.Packet;

import java.io.IOException;
import java.util.Scanner;

/**
 * The type Hello application.
 */
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws IOException the io exception
     */
    public static void main(String[] args) throws IOException {
        Chessboard chessboard = new Chessboard();
        //connectionTest();
        //launch();
    }

    private static void connectionTest() throws IOException {
        NetworkManager.setup("127.0.0.1", 5000);

        Player c = new Player();

        Scanner sc = new Scanner(System.in);

        String line = "";

        while(!line.equalsIgnoreCase("exit")){
            line = sc.nextLine();
            String finalLine = line;
            //c.sendPacket(null);
            //System.out.println("Recvd: " + recv);
        }
        c.disconnect();
    }
}