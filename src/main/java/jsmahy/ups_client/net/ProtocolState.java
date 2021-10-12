package jsmahy.ups_client.net;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.in.PacketLobbyInHandshake;
import jsmahy.ups_client.net.out.PacketLobbyOutHandshake;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public enum ProtocolState {
    LOBBY {
        {
            register(PacketDirection.SERVER_BOUND, PacketLobbyOutHandshake.class, 0x00);
            register(PacketDirection.CLIENT_BOUND, PacketLobbyInHandshake.class, 0x80);
        }
    },
    PLAY {
        {
        }
    };

    private final Map<PacketDirection, Map<Integer, Class<? extends Packet>>> PACKET_REGISTRY_BY_ID = new HashMap<>();
    private final Map<PacketDirection, Map<Class<? extends Packet>, Integer>> PACKET_REGISTRY_BY_CLASS =
            new HashMap<>();


    public static ProtocolState getById(int state) {
        if (!isValidState(state)) {
            throw new IllegalArgumentException(String.format("Invalid state %d", state));
        }
        return values()[state];
    }

    private static boolean isValidState(int state) {
        return state >= 0 && state < values().length;
    }

    /**
     * Attempts to instantiate a packet sent from server
     *
     * @param packetId the packet id
     *
     * @return an instance of a packet
     * @throws InvalidPacketFormatException if the packet format is incorrect
     */
    public Packet getPacket(PacketDirection direction, int packetId) throws
            InvalidPacketFormatException {
        final Map<Integer, Class<? extends Packet>> registry = PACKET_REGISTRY_BY_ID.get(direction);
        if (registry == null || !registry.containsKey(packetId)) {
            throw new InvalidPacketFormatException(String.format("No packet with ID %d and direction %s found in %s state!",
                    packetId
                    , direction, this));
        }

        try {
            return registry.get(packetId).getConstructor().newInstance();
        } catch (Exception e) {
            // LOG ERROR
            throw new IllegalStateException(
                    String.format("Could not instantiate a packet with ID %d, direction %s, and state %s", packetId,
                            direction, this));
        }
    }


    /**
     * @param direction
     * @param packetClass
     *
     * @return
     *
     * @throws IllegalArgumentException
     */
    public int getPacketId(PacketDirection direction, Class<?
            extends Packet> packetClass) throws IllegalArgumentException {
        Map<Class<? extends Packet>, Integer> map = PACKET_REGISTRY_BY_CLASS.get(direction);
        if (map == null || !map.containsKey(packetClass)) {
            throw new IllegalArgumentException(String.format("No packet %s registered in %s direction and state %s!",
                    packetClass.getSimpleName(), direction, this));
        }
        return map.get(packetClass);
    }

    protected void register(PacketDirection direction, Class<? extends Packet> packetClass, int packetId) {
        putId(direction, packetClass, packetId);
        putClass(direction, packetClass, packetId);
    }

    private void putClass(final PacketDirection direction, final Class<? extends Packet> packetClass,
                          final int packetId) {
        final Map<Class<? extends Packet>, Integer> map =
                PACKET_REGISTRY_BY_CLASS.getOrDefault(direction, new HashMap<>());
        map.putIfAbsent(packetClass, packetId);
        PACKET_REGISTRY_BY_CLASS.putIfAbsent(direction, map);
    }

    private void putId(final PacketDirection direction, final Class<? extends Packet> packetClass,
                       final int packetId) {
        final Map<Integer, Class<? extends Packet>> map =
                PACKET_REGISTRY_BY_ID.getOrDefault(direction, new HashMap<>());
        map.putIfAbsent(packetId, packetClass);
        PACKET_REGISTRY_BY_ID.putIfAbsent(direction, map);
    }
}
