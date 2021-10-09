package jsmahy.ups_client.net.in;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author Doomshade
 * @version 1.0
 * @since 1.0
 */
public class PacketLobbyInHandshake implements PacketInLobby {
    private ResponseCode responseCode = null;

    @Override
    public void read(final DataInputStream in) throws IOException {
        // we only need the last bit right now as we only have two response codes
        final int b = in.readUnsignedByte() & 0b1;
        this.responseCode = ResponseCode.values()[b];
    }

    @Override
    public void broadcast(final PacketListenerLobby listener) {
        listener.onHandshake(this);
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }
}
