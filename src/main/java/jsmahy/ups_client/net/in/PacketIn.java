package jsmahy.ups_client.net.in;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.Packet;
import jsmahy.ups_client.net.listener.PacketListener;

/**
 * An incoming packet Server -{@literal >} Client.
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public interface PacketIn<T extends PacketListener> extends Packet {

    /**
     * Reads the packet from the input String array.
     *
     * @param in the String array
     *
     * @throws InvalidPacketFormatException if thr received packet has an invalid format
     */
    void read(String[] in) throws InvalidPacketFormatException;

    void broadcast(T listener) throws InvalidPacketFormatException;
}
