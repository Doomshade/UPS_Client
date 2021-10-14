package jsmahy.ups_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jsmahy.ups_client.game.ChessPlayer;
import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.in.PlayerConnection;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.simple.SimpleLogger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * The type Hello application.
 */
public class HelloApplication extends Application {
    private static final SimpleLogger LOGGER =
            (SimpleLogger) LogManager.getLogger(HelloApplication.class);

    public static Logger getLogger() {
        return LOGGER;
    }

    @Override
    public void start(Stage stage) throws IOException {
        LOGGER.setLevel(Level.DEBUG);
        Chessboard chessboard = new Chessboard();
        FXMLLoader fxmlLoader =
                new FXMLLoader(HelloApplication.class.getResource("/fxml/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     *
     * @throws IOException the io exception
     */
    public static void main(String[] args) throws IOException {
        launch(args);
        //connectionTest();
        //launch();
    }

    private static void connectionTest() throws IOException {
        NetworkManager.setup(InetAddress.getLocalHost().getHostAddress(), 5000);

        ChessPlayer white = new ChessPlayer("Testshade", true);
        PlayerConnection c = new PlayerConnection(white);

        Scanner sc = new Scanner(System.in);

        String line = "";

        while (!line.equalsIgnoreCase("exit")) {
            line = sc.nextLine();
            String finalLine = line;
            //c.sendPacket(null);
            //System.out.println("Recvd: " + recv);
        }
        c.disconnect();
    }
}