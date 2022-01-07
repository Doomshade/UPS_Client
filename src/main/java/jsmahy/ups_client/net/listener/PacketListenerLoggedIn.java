package jsmahy.ups_client.net.listener;

import jsmahy.ups_client.net.in.logged_in.packet.PacketLoggedInInJoinQueue;

public interface PacketListenerLoggedIn extends PacketListener {
    void onQueue(PacketLoggedInInJoinQueue packet);
}
