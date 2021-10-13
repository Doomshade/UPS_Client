package jsmahy.ups_client.net.in;

import org.apache.commons.lang3.builder.ToStringBuilder;

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
        this.responseCode = ResponseCode.getResponseCode(in.readUTF());
    }

    @Override
    public void broadcast(final PacketListenerLobby listener) {
        listener.onHandshake(this);
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("responseCode", responseCode)
                .toString();
    }
}
