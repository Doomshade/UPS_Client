package jsmahy.ups_client.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The type Packet.
 */
public interface Packet {

    void write(DataOutputStream out) throws IOException;

    void read(DataInputStream in) throws IOException;

    int getId();
}
