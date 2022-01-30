/**
 * @author Jakub Šmrha
 */
module UPS.Client {
	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires org.apache.logging.log4j;

	requires org.apache.commons.lang3;
	requires org.jetbrains.annotations;

	exports jsmahy.ups_client;
	exports jsmahy.ups_client.controller;
	exports jsmahy.ups_client.exception;
	exports jsmahy.ups_client.game;
	exports jsmahy.ups_client.net;
	exports jsmahy.ups_client.net.in;
	exports jsmahy.ups_client.net.out;
	exports jsmahy.ups_client.net.listener;
	exports jsmahy.ups_client.net.listener.impl;
	exports jsmahy.ups_client.util;

	opens jsmahy.ups_client;
	opens jsmahy.ups_client.controller;
	opens jsmahy.ups_client.exception;
	opens jsmahy.ups_client.game;
	opens jsmahy.ups_client.net;
	opens jsmahy.ups_client.net.in;
	opens jsmahy.ups_client.net.out;
	opens jsmahy.ups_client.net.listener;
	opens jsmahy.ups_client.net.listener.impl;
	opens jsmahy.ups_client.util;
	exports jsmahy.ups_client.net.in.all;
	opens jsmahy.ups_client.net.in.all;
}