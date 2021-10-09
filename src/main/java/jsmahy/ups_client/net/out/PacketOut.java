package jsmahy.ups_client.net.out;

import jsmahy.ups_client.net.Packet;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Doomshade
 * @version 1.0
 * @since 1.0
 */
public interface PacketOut extends Packet {
    void write(DataOutputStream out) throws IOException;
}
