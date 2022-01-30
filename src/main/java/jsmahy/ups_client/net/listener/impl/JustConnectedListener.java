package jsmahy.ups_client.net.listener.impl;

import jsmahy.ups_client.SceneManager;
import jsmahy.ups_client.controller.ServerConnectionController;
import jsmahy.ups_client.exception.InvalidProtocolStateException;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.ProtocolState;
import jsmahy.ups_client.net.in.just_connected.packet.PacketJustConnectedInHello;

/**
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public final class JustConnectedListener extends AbstractListener {

	{
		register(PacketJustConnectedInHello.class, this::onHello);
	}

	private void onHello(final PacketJustConnectedInHello packet) {
		switch (packet.getResponseCode()) {
			case OK:
				Client.login();
				NetworkManager.getInstance().changeState(ProtocolState.LOGGED_IN);
				SceneManager.changeScene(SceneManager.Scenes.MAIN_MENU);
				startKeepAlive();
				break;
			case REJECTED:
				NetworkManager.getInstance().disconnect(null, null, null, true);
				ServerConnectionController.getInstance().errorUsernameExists();
				break;
			default:
				throw new InvalidProtocolStateException("Received an invalid message.");
		}
	}
}
