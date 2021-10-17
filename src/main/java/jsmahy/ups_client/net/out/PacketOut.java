package jsmahy.ups_client.net.out;

import jsmahy.ups_client.net.Packet;

import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * Interface for outgoing packets.
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public interface PacketOut extends Packet {

    /**
     * Writes the packet data to the output stream.
     *
     * @param out the output stream
     *
     * @throws IOException if the data could not be written into the stream
     */
    void write(final BufferedOutputStream out) throws IOException;
}
