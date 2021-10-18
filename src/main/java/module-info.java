/**
 * @author Jakub Šmrha
 */module UPS.Client {
    requires javafx.base;
    requires javafx.media;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;
    requires org.apache.logging.log4j;

    requires java.logging;
    requires org.apache.commons.lang3;
    requires org.jetbrains.annotations;

    exports jsmahy.ups_client;
    exports jsmahy.ups_client.controller;

    opens jsmahy.ups_client;
    opens jsmahy.ups_client.controller;
}