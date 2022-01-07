package jsmahy.ups_client.net;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.in.just_connected.packet.PacketJustConnectedInHello;
import jsmahy.ups_client.net.in.logged_in.packet.PacketLoggedInInJoinQueue;
import jsmahy.ups_client.net.in.play.packet.*;
import jsmahy.ups_client.net.in.queue.packet.PacketQueueInGameStart;
import jsmahy.ups_client.net.out.just_connected.PacketJustConnectedOutHello;
import jsmahy.ups_client.net.out.logged_in.PacketLoggedInOutJoinQueue;
import jsmahy.ups_client.net.out.play.PacketPlayOutDrawOffer;
import jsmahy.ups_client.net.out.play.PacketPlayOutMessage;
import jsmahy.ups_client.net.out.play.PacketPlayOutMove;
import jsmahy.ups_client.net.out.play.PacketPlayOutResign;
import jsmahy.ups_client.net.out.queue.PacketQueueOutLeaveQueue;
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
    JUST_CONNECTED {
        {
            // server bound
            register(PacketJustConnectedOutHello.class, JUST_CONNECTED_OFFSET);

            // client bound
            register(PacketJustConnectedInHello.class, JUST_CONNECTED_OFFSET + PACKET_IN_OFFSET);
        }
    },
    LOGGED_IN {
        {
            // server bound
            register(PacketLoggedInOutJoinQueue.class, LOGGED_IN_OFFSET);

            // client bound
            register(PacketLoggedInInJoinQueue.class, LOGGED_IN_OFFSET + PACKET_IN_OFFSET);
        }
    },
    QUEUE {
        {
            // server bound
            register(PacketQueueOutLeaveQueue.class, QUEUE_OFFSET);

            // client bound
            register(PacketQueueInGameStart.class, QUEUE_OFFSET + PACKET_IN_OFFSET);
        }
    },
    PLAY {
        {
            // server bound
            register(PacketPlayOutMove.class, PLAY_OFFSET);
            register(PacketPlayOutDrawOffer.class, PLAY_OFFSET + 0x01);
            register(PacketPlayOutResign.class, PLAY_OFFSET + 0x02);
            register(PacketPlayOutMessage.class, PLAY_OFFSET + 0x03);

            // client bound
            register(PacketPlayInMove.class, PLAY_OFFSET + PACKET_IN_OFFSET);
            register(PacketPlayInDrawOffer.class, PLAY_OFFSET + PACKET_IN_OFFSET + 0x01);
            register(PacketPlayInGameFinish.class, PLAY_OFFSET + PACKET_IN_OFFSET + 0x03);
            register(PacketPlayInMessage.class, PLAY_OFFSET + PACKET_IN_OFFSET + 0x04);
            register(PacketPlayInKeepAlive.class, PLAY_OFFSET + PACKET_IN_OFFSET + 0x05);
        }
    };

    private static final int JUST_CONNECTED_OFFSET = 0x00;
    private static final int LOGGED_IN_OFFSET = 0x20;
    private static final int QUEUE_OFFSET = 0x40;
    private static final int PLAY_OFFSET = 0x60;
    private static final int PACKET_IN_OFFSET = 0x80;

    private static Logger L = null;

    /**
     * The ID-packet map.
     */
    private final Map<ProtocolState, Map<Integer, Class<? extends Packet>>>
            packetRegistryById = new HashMap<>();

    /**
     * The packet-ID map.
     */
    private final Map<ProtocolState, Map<Class<? extends Packet>, Integer>>
            packetRegistryByClass = new HashMap<>();


    /**
     * Gets the protocol state by ID.
     *
     * @param state the state ID
     * @return the protocol state based on the ID
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
     * @return {@code true} if it's in bounds of the protocol states
     */
    private static boolean isValidState(int state) {
        return state >= 0 && state < values().length;
    }

    /**
     * Attempts to instantiate a packet.
     *
     * @param packetId the packet id
     * @return an instance of a packet
     * @throws InvalidPacketFormatException if the packet format is incorrect
     * @throws IllegalStateException        if the packet class could not be instantiated with the
     *                                      default constructor
     */
    public Packet getPacket(int packetId) throws
            InvalidPacketFormatException {
        final Map<Integer, Class<? extends Packet>> registry = packetRegistryById.get(this);
        if (registry == null || !registry.containsKey(packetId)) {
            throw new InvalidPacketFormatException(
                    format("No packet with ID %d found in %s state!", packetId, this));
        }

        try {
            return registry.get(packetId).getConstructor().newInstance();
        } catch (Exception e) {
            String msg = "Could not instantiate a packet with ID %d, and state %s";
            L.error(format(msg, packetId, this), e);
            throw new IllegalStateException(msg, e);
        }
    }


    /**
     * Attempts to retrieve the packet's ID based on the direction and the class.
     *
     * @param direction   the packet direction
     * @param packetClass the packet's class
     * @return the packet ID
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
     * @param packetClass the packet class
     * @param packetId    the packet id
     */
    protected void register(Class<? extends Packet> packetClass,
                            int packetId) {
        if (L == null) {
            L = LogManager.getLogger(ProtocolState.class);
        }
        putId(packetClass, packetId);
        putClass(packetClass, packetId);
        L.debug(format("Registered %s packet with id 0x%x",
                packetClass.getSimpleName(), packetId));
    }

    /**
     * Registers a packet in the class-id map.
     *
     * @param packetClass the packet's class
     * @param packetId    the packet id
     */
    private void putClass(final Class<? extends Packet> packetClass,
                          final int packetId) {
        putPacket(packetRegistryByClass, this, packetClass, packetId);

    }

    /**
     * Registers a packet in the id-class map.
     *
     * @param packetClass the packet's class
     * @param packetId    the packet id
     */
    private void putId(final Class<? extends Packet> packetClass,
                       final int packetId) {
        putPacket(packetRegistryById, this, packetId, packetClass);
    }

    /**
     * Puts a packet in the given map.
     *
     * @param map     the map
     * @param mainKey the main key
     * @param key     the key
     * @param value   the value
     * @param <MK>    the main key type
     * @param <K>     the key type
     * @param <V>     the value type
     * @throws IllegalStateException if the packet has already been registered in the map
     */
    private <MK, K, V> void putPacket(Map<MK, Map<K, V>> map, MK mainKey,
                                      K key, V value) throws IllegalStateException {
        final Map<K, V> m = map.getOrDefault(mainKey, new HashMap<>());
        final V prev = m.putIfAbsent(key, value);
        if (prev != null) {
            throw new IllegalStateException(format("Attempted to register a packet in a map " +
                            "(key=%s, value=%s) in %s state, but it already exists!", key,
                    value, mainKey));
        }
        map.putIfAbsent(mainKey, m);
    }
}
