package jsmahy.ups_client.net.listener.impl;

import jsmahy.ups_client.SceneManager;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.ProtocolState;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.logged_in.packet.PacketLoggedInInJoinQueue;
import jsmahy.ups_client.net.in.logged_in.packet.PacketLoggedInInReconnect;

/**
 * The listener for the {@link ProtocolState#LOGGED_IN} state
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class LoggedInListener extends AbstractListener {

	{
		register(PacketLoggedInInJoinQueue.class, this::onQueue);
		register(PacketLoggedInInReconnect.class, this::onReconnect);
	}


	private void onQueue(PacketLoggedInInJoinQueue packet) {
		if (packet.getResponseCode() == ResponseCode.OK) {
			NetworkManager.getInstance().changeState(ProtocolState.QUEUE);
			SceneManager.changeScene(SceneManager.Scenes.PLAY_SCENE);
		}
	}

	private void onReconnect(PacketLoggedInInReconnect packet) {
		NetworkManager.getInstance().changeState(ProtocolState.QUEUE);
	}
}
