package jsmahy.ups_client.net.in.logged_in.packet;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.in.logged_in.PacketInLoggedIn;
import jsmahy.ups_client.net.listener.PacketListenerLoggedIn;

public class PacketLoggedInInJoinQueue implements PacketInLoggedIn {
    @Override
    public void read(String in) throws InvalidPacketFormatException {
        // TODO
    }

    @Override
    public void broadcast(PacketListenerLoggedIn listener) throws InvalidPacketFormatException {
        listener.onQueue(this);
    }
}
