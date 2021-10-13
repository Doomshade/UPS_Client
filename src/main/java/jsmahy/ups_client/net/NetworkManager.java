package jsmahy.ups_client.net;

import jsmahy.ups_client.HelloApplication;
import jsmahy.ups_client.net.in.*;
import jsmahy.ups_client.net.out.PacketOut;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 * The network manager that handles packets and broadcasts them to listeners
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @see PacketListener
 * @see LobbyListener
 * @see PlayerConnection
 * @since 1.0
 */
public final class NetworkManager {
    private static final Logger L = HelloApplication.getLogger();

    private static final NetworkManager INSTANCE = new NetworkManager();

    /**
     * The packet listener based on the protocol state.
     */
    private static final Map<ProtocolState, PacketListener> LISTENERS = new HashMap<>() {
        {
            put(ProtocolState.LOBBY, new LobbyListener());
        }
    };

    /**
     * The timeout limit for server to answer.
     */
    private static final int TIMEOUT = 30_000;

    /**
     * Indicates, whether the class was successfully instantiated.
     */
    private static boolean successfullyInitialized = false;

    private String host;
    private int port;
    private Socket socket = null;

    /**
     * Messages from server are received by this input stream.
     */
    private DataInputStream in = null;

    /**
     * Messages to server are sent in this output stream.
     */
    private DataOutputStream out = null;

    /**
     * The current state of the protocol.
     */
    private ProtocolState state = ProtocolState.LOBBY;

    private NetworkManager() {
    }

    /**
     * Sets up (initializes) the network manager.
     *
     * @param host the host
     * @param port the port
     *
     * @throws IOException if the connection could not be established
     */
    public static void setup(final String host, final int port) throws IOException {
        L.info(format("Setting up Network Manager with host %s and port %d", host, port));
        INSTANCE.setup0(host, port);
    }

    /**
     * Sets up (initializes) the network manager.
     *
     * @param host the host
     * @param port the port
     *
     * @throws IOException if the connection could not be established
     */
    private void setup0(final String host, final int port) throws IOException {
        this.host = host;
        this.port = port;
        initializeConnection();
    }

    /**
     * Initializes the connection between the client and the server.
     *
     * @throws IOException if the connection could not be established
     */
    private void initializeConnection() throws IOException {
        this.socket = new Socket(host, port);
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());
        this.socket.setSoTimeout(TIMEOUT);
        this.socket.setKeepAlive(true);
        L.info("Successfully initialized connection");

        startListening();

        successfullyInitialized = true;
    }

    private void startListening() {
        Thread readThread = new Thread(new PacketDeserializer(this.in));
        readThread.setDaemon(true);
        readThread.start();
    }

    /**
     * Gets the packet listener based on the current state.
     *
     * @param <T> the packet listener implementation
     *
     * @return the packet listener
     */
    public <T extends PacketListener> T getCurrentListener() {
        return (T) getListener(state);
    }

    /**
     * Gets the packet listener based on the given state.
     *
     * @param state the state
     *
     * @return the packet listener
     */
    private PacketListener getListener(ProtocolState state) {
        return LISTENERS.get(state);
    }

    /**
     * Sends a packet to the server.
     *
     * @param packet the packet to send
     */
    public void sendPacket(PacketOut packet) {
        try {
            // Packet format: [ID | State | Data]

            // ID
            final int id = state.getPacketId(PacketDirection.SERVER_BOUND, packet.getClass());
            out.writeByte(id);

            // State
            // TODO delete state
            out.writeByte(state.ordinal());

            // Data
            packet.write(out);

            // write
            out.flush();
        } catch (IOException e) {
            L.error("Could not write to the output stream!", e);
        }
    }

    /**
     * Sets up the packet listener in {@link ProtocolState#PLAY} state.
     *
     * @param listener the listener
     */
    public static void setPlayListener(PacketListenerPlay listener) {
        LISTENERS.put(ProtocolState.PLAY, listener);
    }

    /**
     * @return the instance
     */
    public static NetworkManager getInstance() {
        return INSTANCE;
    }

    /**
     * @return {@code true} if the network manager has been successfully initialized.
     */
    public static boolean isSuccessfullyInitialized() {
        return successfullyInitialized;
    }

    /**
     * Changes the current state of the client.
     *
     * @param state the state to change to
     */
    public void changeState(ProtocolState state) {
        this.state = state;
    }

    /**
     * @return current client state
     */
    public ProtocolState getState() {
        return state;
    }

    /**
     * Stops listening to the server - closes the socket
     */
    public void stopListening() throws IOException {
        socket.close();
    }
}
