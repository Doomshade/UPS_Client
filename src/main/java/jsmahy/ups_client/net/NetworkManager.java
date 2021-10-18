package jsmahy.ups_client.net;

import jsmahy.ups_client.net.in.*;
import jsmahy.ups_client.net.out.PacketOut;
import jsmahy.ups_client.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
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
    private static final Logger L = LogManager.getLogger(NetworkManager.class);

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
     * Indicates whether the connection was successfully instantiated.
     */
    private boolean connectionSuccessful = false;
    private Socket socket = null;
    /**
     * Messages from server are received by this input stream.
     */
    private BufferedInputStream in = null;

    /**
     * Messages to server are sent in this output stream.
     */
    private BufferedOutputStream out = null;

    /**
     * Indicates whether the I/O streams were initialized.
     */
    private boolean initializedStreams = false;

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
     * @throws IOException           if the connection could not be established
     * @throws IllegalStateException if the connection has already been established
     */
    public void setup(final String host, final int port)
            throws IOException, IllegalStateException {
        if (isConnectionSuccessful()) {
            throw new IllegalStateException("Already connected to a server!");
        }
        L.info(format("Setting up Network Manager with host %s and port %d", host, port));
        this.socket = new Socket(host, port);
        this.socket.setSoTimeout(TIMEOUT);
        this.socket.setKeepAlive(true);
        this.setupIO0(socket.getInputStream(), socket.getOutputStream());

        connectionSuccessful = true;
        L.info("Successfully initialized connection");
    }

    private void startListening() {
        Thread readThread = new Thread(new PacketDeserializer(this.in));
        readThread.setDaemon(true);
        readThread.start();
    }

    /**
     * Sets up the I/O streams.
     *
     * @param in  the input stream
     * @param out the output stream
     *
     * @throws IllegalStateException if the streams have already been initialized
     */
    public void setupIO0(@NotNull final InputStream in, @NotNull final OutputStream out)
            throws IllegalStateException {
        if (isInitializedStreams()) {
            throw new IllegalStateException("I/O streams have already been initialized!");
        }
        L.info("Setting up I/O...");
        this.in = new BufferedInputStream(in);
        this.out = new BufferedOutputStream(out);
        startListening();
        this.initializedStreams = true;
        L.info("I/O set up");
    }

    /**
     * @return {@code true} if the I/O streams have been successfully initialized.
     */
    public boolean isInitializedStreams() {
        return initializedStreams;
    }

    /**
     * @return {@code true} if the connection has been successfully established.
     */
    public boolean isConnectionSuccessful() {
        return connectionSuccessful;
    }

    /**
     * Sets up the packet listener in {@link ProtocolState#PLAY} state.
     *
     * @param listener the listener
     */
    public static void setPlayListener(@NotNull PacketListenerPlay listener) {
        LISTENERS.put(ProtocolState.PLAY, listener);
    }

    /**
     * @return the instance
     */
    public static NetworkManager getInstance() {
        return INSTANCE;
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
    private PacketListener getListener(@NotNull ProtocolState state) {
        return LISTENERS.get(state);
    }

    /**
     * Sends a packet to the server.
     *
     * @param packet the packet to send
     *
     * @throws IllegalStateException if the network manager has not yet been initialized
     */
    public void sendPacket(@NotNull PacketOut packet) throws IllegalStateException {
        if (!isInitializedStreams()) {
            throw new IllegalStateException("The I/O streams have not yet been initialized!");
        }
        try {
            // Packet format: [ID;Data]

            // ID
            final int id = state.getPacketId(PacketDirection.SERVER_BOUND, packet.getClass());
            out.write(id);
            out.write(Util.SEPARATION_CHAR);

            // Data
            packet.write(out);

            // write
            out.flush();
        } catch (IOException e) {
            L.error("Could not write to the output stream!", e);
        }
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
     * Stops listening to the server - closes the socket and the I/O streams
     */
    public void stopListening() throws IOException {
        if (socket != null) {
            socket.close();
        }
        if (out != null) {
            out.close();
        }
        if (in != null) {
            in.close();
        }
        connectionSuccessful = false;
        initializedStreams = false;
    }
}
