package jsmahy.ups_client;

import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Label;

/**
 * The type Hello controller.
 */
public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    private AreaChart areaChart;

    /**
     * On hello button click.
     */
    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onDragDetected() {

    }
}