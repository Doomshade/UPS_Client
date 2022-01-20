package jsmahy.ups_client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jsmahy.ups_client.game.ChessPlayer;
import jsmahy.ups_client.game.Chessboard;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.ProtocolState;
import jsmahy.ups_client.util.Square;
import jsmahy.ups_client.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;

/**
 * The type Hello application.
 */
public class Main extends Application {
	private static final Logger L = LogManager.getLogger(Main.class);
	private static Stage stage = null;

	/**
	 * The entry point of application.
	 *
	 * @param args the input arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	private static void connectionTest() throws IOException {
		NetworkManager.getInstance().setup(InetAddress.getLocalHost().getHostAddress(), 5000, null, null);
	}

	public static Stage getStage() {
		return stage;
	}

	@Override
	public void start(Stage stage) throws IOException {
		Main.stage = stage;
		NetworkManager.getInstance().changeState(ProtocolState.JUST_CONNECTED);
		stage.setOnCloseRequest(x -> {
			Platform.exit();
			System.exit(0);
		});
		stage.setTitle("Semestrální práce - Šachy (Jakub Šmrha jsmahy@students.zcu.cz)");
		stage.show();
	}
}