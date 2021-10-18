package jsmahy.ups_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jsmahy.ups_client.game.ChessGame;
import jsmahy.ups_client.game.ChessPlayer;
import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.ProtocolState;
import jsmahy.ups_client.net.in.PlayerConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetAddress;

/**
 * The type Hello application.
 */
public class HelloApplication extends Application {
    private static final Logger L = LogManager.getLogger(HelloApplication.class);

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
        NetworkManager.getInstance().setup(InetAddress.getLocalHost().getHostAddress(), 5000);

        ChessPlayer white = new ChessPlayer("Testshade");
        PlayerConnection c = new PlayerConnection(white);
        c.disconnect();
    }

    @Override
    public void start(Stage stage) throws IOException {
        Chessboard chessboard = new Chessboard();
        testPackets();
        FXMLLoader fxmlLoader =
                new FXMLLoader(HelloApplication.class.getResource("/fxml/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    private void testPackets() throws IOException {
        PlayerConnection con = new PlayerConnection(new ChessPlayer("test"));

        ChessGame chessGame = new ChessGame(new Chessboard(), con, new ChessPlayer("test2"), true);
        con.startGame(chessGame);
        NetworkManager.getInstance().changeState(ProtocolState.PLAY);
        File f = new File("C:\\Temp\\ups\\testt.txt");
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
        NetworkManager.getInstance().setupIO0(System.in, System.out);
        /*// packet ID
        out.write(
                "81".concat(String.valueOf(Util.SEPARATION_CHAR)).getBytes(StandardCharsets.UTF_8));
        // response code
        out.write(
                (ResponseCode.MOVE.name() + Util.SEPARATION_CHAR).getBytes(StandardCharsets.UTF_8));
        // move
        String msg = String.format("%s%c%s", "A2", Util.SEPARATION_CHAR, "A4");
        out.write(msg.getBytes(StandardCharsets.UTF_8));
        out.flush();

        PacketDeserializer deserializer = new PacketDeserializer(new FileInputStream(f));
        deserializer.run();*/
    }
}