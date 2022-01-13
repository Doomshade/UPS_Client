package jsmahy.ups_client.net.listener.impl;

import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.ProtocolState;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.logged_in.packet.PacketLoggedInInJoinQueue;
import jsmahy.ups_client.net.in.logged_in.packet.PacketLoggedInInReconnect;

public class LoggedInListener extends AbstractListener {

    private static final NetworkManager NM = NetworkManager.getInstance();

    {
        register(PacketLoggedInInJoinQueue.class, this::onQueue);
        register(PacketLoggedInInReconnect.class, this::onReconnect);
    }


    private void onQueue(PacketLoggedInInJoinQueue packet) {
        if (packet.getResponseCode() == ResponseCode.OK) {
            NM.changeState(ProtocolState.QUEUE);
            // TODO change the window
        }
    }

    private void onReconnect(PacketLoggedInInReconnect packet) {
        NM.changeState(ProtocolState.PLAY);
    }
}
