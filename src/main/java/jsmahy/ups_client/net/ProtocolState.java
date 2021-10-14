package jsmahy.ups_client.net;

import jsmahy.ups_client.HelloApplication;
import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.in.PacketLobbyInHandshake;
import jsmahy.ups_client.net.out.PacketLobbyOutHandshake;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 * An enumeration of protocol states. Works as a packet registry as well
 *
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

    private static final Logger L = LogManager.getLogger(ProtocolState.class);
    // a BiMap would be nice here
    /**
     * The ID-packet map.
     */
    private final Map<PacketDirection, Map<Integer, Class<? extends Packet>>>
            packetRegistryById = new HashMap<>();

    /**
     * The packet-ID map.
     */
    private final Map<PacketDirection, Map<Class<? extends Packet>, Integer>>
            packetRegistryByClass = new HashMap<>();


    /**
     * Gets the protocol state by ID.
     *
     * @param state the state ID
     *
     * @return the protocol state based on the ID
     *
     * @throws IllegalArgumentException if the state ID does not exist
     */
    public static ProtocolState getById(final int state) throws IllegalArgumentException {
        if (!isValidState(state)) {
            throw new IllegalArgumentException(format("Invalid state %d", state));
        }
        return values()[state];
    }

    /**
     * Checks whether the state ID is valid.
     *
     * @param state the state ID
     *
     * @return {@code true} if it's in bounds of the protocol states
     */
    private static boolean isValidState(int state) {
        return state >= 0 && state < values().length;
    }

    /**
     * Attempts to instantiate a packet.
     *
     * @param direction the packet direction
     * @param packetId  the packet id
     *
     * @return an instance of a packet
     *
     * @throws InvalidPacketFormatException if the packet format is incorrect
     * @throws IllegalStateException        if the packet class could not be instantiated with the
     *                                      default constructor
     */
    public Packet getPacket(PacketDirection direction, int packetId) throws
            InvalidPacketFormatException {
        final Map<Integer, Class<? extends Packet>> registry = packetRegistryById.get(direction);
        if (registry == null || !registry.containsKey(packetId)) {
            throw new InvalidPacketFormatException(
                    format("No packet with ID %d and direction %s found in %s state!", packetId,
                            direction, this));
        }

        try {
            return registry.get(packetId).getConstructor().newInstance();
        } catch (Exception e) {
            String msg = "Could not instantiate a packet with ID %d, direction %s, and state %s";
            L.error(format(msg, packetId, direction, this), e);
            throw new IllegalStateException(msg, e);
        }
    }


    /**
     * Attempts to retrieve the packet's ID based on the direction and the class.
     *
     * @param direction   the packet direction
     * @param packetClass the packet's class
     *
     * @return the packet ID
     *
     * @throws IllegalArgumentException if the packet is not registered
     */
    public int getPacketId(PacketDirection direction, Class<?
            extends Packet> packetClass) throws IllegalArgumentException {
        Map<Class<? extends Packet>, Integer> map = packetRegistryByClass.get(direction);
        if (map == null || !map.containsKey(packetClass)) {
            throw new IllegalArgumentException(
                    format("No packet %s registered in %s direction and state %s!",
                            packetClass.getSimpleName(), direction, this));
        }
        return map.get(packetClass);
    }

    /**
     * Registers a packet.
     *
     * @param direction   the packet direction
     * @param packetClass the packet class
     * @param packetId    the packet id
     */
    protected void register(PacketDirection direction, Class<? extends Packet> packetClass,
                            int packetId) {
        putId(direction, packetClass, packetId);
        putClass(direction, packetClass, packetId);
    }

    /**
     * Registers a packet in the class-id map.
     *
     * @param direction   the packet direction
     * @param packetClass the packet's class
     * @param packetId    the packet id
     */
    private void putClass(final PacketDirection direction,
                          final Class<? extends Packet> packetClass,
                          final int packetId) {
        putPacket(packetRegistryByClass, direction, packetClass, packetId);
        L.debug(format("Registered %s packet in %s direction with id %d",
                packetClass.getSimpleName(), direction, packetId));
    }

    /**
     * Registers a packet in the id-class map.
     *
     * @param direction   the packet direction
     * @param packetClass the packet's class
     * @param packetId    the packet id
     */
    private void putId(final PacketDirection direction, final Class<? extends Packet> packetClass,
                       final int packetId) {
        putPacket(packetRegistryById, direction, packetId, packetClass);
        L.debug(format("Registered %s packet in %s direction with id %d",
                packetClass.getSimpleName(), direction, packetId));
    }

    /**
     * Puts a packet in the given map.
     *
     * @param map       the map
     * @param direction the direction
     * @param key       the key
     * @param value     the value
     * @param <K>       the key type
     * @param <V>       the value type
     *
     * @throws IllegalStateException if the packet has already been registered in the map
     */
    private <K, V> void putPacket(Map<PacketDirection, Map<K, V>> map, PacketDirection direction,
                                  K key, V value) throws IllegalStateException {
        final Map<K, V> m = map.getOrDefault(direction, new HashMap<>());
        final V prev = m.putIfAbsent(key, value);
        if (prev != null) {
            throw new IllegalStateException(format("Attempted to register a packet in a map " +
                            "(key=%s, value=%s) in %s direction, but it already exists!", key,
                    value, direction));
        }
        map.putIfAbsent(direction, m);
    }
}
