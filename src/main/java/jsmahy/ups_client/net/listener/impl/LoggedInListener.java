package jsmahy.ups_client.net.listener.impl;

import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.ProtocolState;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.logged_in.packet.PacketLoggedInInJoinQueue;
import jsmahy.ups_client.net.in.logged_in.packet.PacketLoggedInInReconnect;

public class LoggedInListener extends AbstractListener {

    {
        register(PacketLoggedInInJoinQueue.class, this::onQueue);
        register(PacketLoggedInInReconnect.class, this::onReconnect);
    }


    private void onQueue(PacketLoggedInInJoinQueue packet) {
        if (packet.getResponseCode() == ResponseCode.OK) {
            NetworkManager.getInstance().changeState(ProtocolState.QUEUE);
            // TODO change the window
        }
    }

    private void onReconnect(PacketLoggedInInReconnect packet) {
        NetworkManager.getInstance().changeState(ProtocolState.PLAY);
    }
}
