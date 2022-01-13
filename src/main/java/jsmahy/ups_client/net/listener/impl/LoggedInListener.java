package jsmahy.ups_client.net.listener.impl;

import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.ProtocolState;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.logged_in.packet.PacketLoggedInInJoinQueue;
import jsmahy.ups_client.net.in.logged_in.packet.PacketLoggedInInReconnect;
import jsmahy.ups_client.net.listener.PacketListenerLoggedIn;

public class LoggedInListener implements PacketListenerLoggedIn {

    private static final NetworkManager NM = NetworkManager.getInstance();

    @Override
    public void onQueue(PacketLoggedInInJoinQueue packet) {
        if (packet.getResponseCode() == ResponseCode.OK) {
            NM.changeState(ProtocolState.QUEUE);
            // TODO change the window
        }
    }

    @Override
    public void onReconnect(PacketLoggedInInReconnect packet) {
        NM.changeState(ProtocolState.PLAY);
    }
}
