/**
 * @author Jakub Šmrha
 */module UPS.Client {
    requires javafx.base;
    requires javafx.media;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;

    requires java.logging;

    exports jsmahy.ups_client;
    exports jsmahy.ups_client.controller;

    opens jsmahy.ups_client;
    opens jsmahy.ups_client.controller;
}