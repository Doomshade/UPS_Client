package jsmahy.ups_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jsmahy.ups_client.game.ChessPlayer;
import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.listener.impl.PlayerConnection;
import jsmahy.ups_client.util.Square;
import jsmahy.ups_client.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetAddress;

/**
 * The type Hello application.
 */
public class Main extends Application {
    private static final Logger L = LogManager.getLogger(Main.class);

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     *
     */
    public static void main(String[] args) {
        launch(args);
    }

    private static void connectionTest() throws IOException {
        ChessPlayer white = new ChessPlayer("Testshade");
        PlayerConnection c = new PlayerConnection(white);
        NetworkManager.getInstance().setup(c, InetAddress.getLocalHost().getHostAddress(), 5000);
        c.disconnect("No reason");
    }

    @Override
    public void start(Stage stage) throws IOException {
        testPieces();
        testPackets();
        FXMLLoader fxmlLoader =
                new FXMLLoader(Main.class.getResource("/fxml/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    private void testPieces() {
        Chessboard board = new Chessboard();
        board.setupBoard(Util.START_FEN);
        final Square p = new Square(1, 4);
        final ChessPlayer pl = new ChessPlayer("test");
        pl.setColour(true);
        for (int i = 0; i < 6; i++) {
            L.debug(board.move(p.add(i, 0), p.add(i + 1, 0), pl));
        }
    }

    private void testPackets() throws IOException {
        PlayerConnection con = new PlayerConnection(new ChessPlayer("test"));

        File f = new File("C:\\Temp\\ups\\testt.txt");
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
        NetworkManager.getInstance().setupIO(con, System.in, out);
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