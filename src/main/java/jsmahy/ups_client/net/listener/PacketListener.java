package jsmahy.ups_client.net.listener;

import jsmahy.ups_client.net.in.PacketIn;

/**
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public interface PacketListener {
    void handle(PacketIn packet);
}
