package jsmahy.ups_client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.ProtocolState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The main entry point
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
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

	public static Stage getStage() {
		return stage;
	}

	@Override
	public void start(Stage stage) {
		Main.stage = stage;
		NetworkManager.getInstance().changeState(ProtocolState.JUST_CONNECTED);
		stage.setOnCloseRequest(x -> {
			Platform.exit();
			System.exit(0);
		});
		stage.setTitle("Semestrální práce - Šachy (Jakub Šmrha jsmahy@students.zcu.cz)");
		stage.setWidth(1000);
		stage.setHeight(750);
		stage.show();
	}
}