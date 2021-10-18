package jsmahy.ups_client.net.in;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.Packet;

import java.io.IOException;

/**
 * @author Doomshade
 * @version 1.0
 * @since 1.0
 */
public interface PacketIn<T extends PacketListener> extends Packet {

    void read(String[] in) throws IOException, InvalidPacketFormatException;

    void broadcast(T listener);
}
