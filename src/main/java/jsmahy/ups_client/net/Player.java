package jsmahy.ups_client.net;

import java.io.IOException;

/**
 * The type Player.
 */
public class Player {

    public Player() {
    }

    /**
     * Send packet.
     *
     * @param packet the packet
     * @throws IOException the io exception
     */
    public String sendPacket(Packet packet) throws IOException {
        return NetworkManager.getInstance().sendPacket(packet);
    }

    /**
     * Disconnect.
     *
     * @throws IOException the io exception
     */
    public void disconnect() throws IOException {

    }
}
