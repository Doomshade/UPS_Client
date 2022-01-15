package jsmahy.ups_client.net.listener.impl;

import jsmahy.ups_client.net.in.PacketIn;
import jsmahy.ups_client.net.listener.PacketListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

abstract class AbstractListener implements PacketListener {
    private final Logger L = LogManager.getLogger(getClass());
    private final Map<Class<? extends PacketIn>, Consumer<? extends PacketIn>> MAP = new HashMap<>();

    protected final <T extends PacketIn> void register(
            Class<T> packetClass,
            Consumer<T> handler) {
        MAP.put(packetClass, handler);
    }

    @Override
    public final void handle(PacketIn packet) {
        final Consumer<PacketIn> handler = (Consumer<PacketIn>) MAP.get(packet.getClass());
        if (handler == null) {
            throw new IllegalStateException("No handler found for " + packet.getClass().getSimpleName());
        }
        L.debug(String.format("Found handler for %s packet in class %s", packet, getClass().getSimpleName()));
        handler.accept(packet);
    }
}
