package jsmahy.ups_client.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public final class AlertBuilder {
	private final Alert alert;

	public AlertBuilder(Alert.AlertType alertType) {
		this.alert = new Alert(alertType);
	}

	public AlertBuilder(Alert.AlertType alertType, String contentText, ButtonType buttons) {
		this.alert = new Alert(alertType, contentText, buttons);
	}

	public AlertBuilder content(String contentText) {
		alert.setContentText(contentText);
		return this;
	}


	public AlertBuilder header(String headerText) {
		alert.setHeaderText(headerText);
		return this;
	}

	public AlertBuilder title(String title) {
		alert.setTitle(title);
		return this;
	}

	public Alert build() {
		return alert;
	}
}
