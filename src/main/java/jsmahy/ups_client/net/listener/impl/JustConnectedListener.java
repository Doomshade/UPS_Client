package jsmahy.ups_client.net.listener.impl;

import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.ProtocolState;
import jsmahy.ups_client.net.in.just_connected.packet.PacketJustConnectedInHello;

/**
 * @author Doomshade
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
                break;
            case REJECTED:
                // TODO prompt the user again
                break;
        }
    }
}
