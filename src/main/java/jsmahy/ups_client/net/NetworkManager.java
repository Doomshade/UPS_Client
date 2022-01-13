package jsmahy.ups_client.net;

import javafx.application.Platform;
import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.in.PacketIn;
import jsmahy.ups_client.net.listener.PacketListener;
import jsmahy.ups_client.net.listener.PacketListenerPlay;
import jsmahy.ups_client.net.listener.impl.Client;
import jsmahy.ups_client.net.listener.impl.JustConnectedListener;
import jsmahy.ups_client.net.listener.impl.LoggedInListener;
import jsmahy.ups_client.net.listener.impl.QueueListener;
import jsmahy.ups_client.net.out.PacketData;
import jsmahy.ups_client.net.out.PacketDataField;
import jsmahy.ups_client.net.out.PacketOut;
import jsmahy.ups_client.util.LoggableOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import static java.lang.String.format;

/**
 * The network manager that handles packets and broadcasts them to listeners
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @see PacketListener
 * @see JustConnectedListener
 * @see Client
 * @since 1.0
 */
public final class NetworkManager {
    public static final String PACKET_MAGIC = "CHESS";
    public static final int PACKET_HEADER_LENGTH = PACKET_MAGIC.length() + 5;
    private static final Logger L = LogManager.getLogger(NetworkManager.class);

    private static final Queue<BufferedPacket> PACKET_QUEUE = new LinkedList<>();

    private static final NetworkManager INSTANCE = new NetworkManager();

    /**
     * The packet listener based on the protocol state.
     */
    private static final Map<ProtocolState, PacketListener> LISTENERS = new HashMap<>() {
        // test
        {
            put(ProtocolState.JUST_CONNECTED, new JustConnectedListener());
            put(ProtocolState.LOGGED_IN, new LoggedInListener());
            put(ProtocolState.QUEUE, new QueueListener());
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
    private ProtocolState state = ProtocolState.JUST_CONNECTED;

    private boolean receivedResponse = true;

    private NetworkManager() {
    }

    /**
     * Sets up the packet listener in {@link ProtocolState#PLAY} state.
     *
     * @param listener the listener
     */
    public static void setClient(@NotNull PacketListenerPlay listener) {
        LISTENERS.put(ProtocolState.PLAY, listener);
    }

    /**
     * @return the instance
     */
    public static NetworkManager getInstance() {
        return INSTANCE;
    }

    /**
     * Sets up (initializes) the network manager.
     *
     * @param host the host
     * @param port the port
     * @throws IOException           if the connection could not be established
     * @throws IllegalStateException if the connection has already been established
     */
    public void setup(@NotNull final String host, final int port)
            throws IOException, IllegalStateException {
        if (isConnectionSuccessful()) {
            throw new IllegalStateException("Already connected to a server!");
        }
        L.info(format("Setting up Network Manager with host %s and port %d", host, port));
        this.socket = new Socket(host, port);
        this.socket.setSoTimeout(TIMEOUT);
        this.socket.setKeepAlive(true);
        this.setupIO(socket.getInputStream(), socket.getOutputStream());

        connectionSuccessful = true;
        L.info("Successfully initialized connection");
    }

    private void startListening(final InputStream in) {
        Thread readThread = new Thread(new PacketDeserializer(in));
        readThread.setDaemon(true);
        readThread.start();
    }

    /**
     * Sets up the I/O streams.
     *
     * @param in  the input stream
     * @param out the output stream
     * @throws IllegalStateException if the streams have already been initialized
     */
    public void setupIO(@NotNull final InputStream in,
                        @Nullable OutputStream out)
            throws IllegalStateException {
        if (isInitializedStreams()) {
            throw new IllegalStateException("I/O streams have already been initialized!");
        }
        L.info("Setting up I/O...");
        this.in = new BufferedInputStream(in);
        if (out == null) {
            try {
                File tempOut = Files.createTempFile("out", null).toFile();
                L.info("Created a new out temp file " + tempOut.getAbsolutePath());
                startListening(new BufferedInputStream(new FileInputStream(tempOut)));
            } catch (IOException e) {
                L.fatal("Could not create a temp file", e);
                throw new RuntimeException(e);
            }
            L.info("Using simulated output stream...");
            this.out = new BufferedOutputStream(System.out);
        } else {
            this.out = new BufferedOutputStream(new LoggableOutputStream(out));
        }
        startListening(in);
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
     * Gets the packet listener based on the current state.
     *
     * @param <T> the packet listener implementation
     * @return the packet listener
     */
    public <T extends PacketListener> T getCurrentListener() {
        return (T) getListener(state);
    }

    /**
     * Gets the packet listener based on the given state.
     *
     * @param state the state
     * @return the packet listener
     */
    private PacketListener getListener(@NotNull ProtocolState state) {
        return LISTENERS.get(state);
    }


    public synchronized void receivePacket(BufferedPacket packet) throws InvalidPacketFormatException, IOException {
        receivePacket(getState().getPacketIn(packet.getPacketId()), packet.getData());
    }

    public synchronized void receivePacket(PacketIn<?> p, String data) throws InvalidPacketFormatException, IOException {
        p.read(data);
        p.broadcast(getCurrentListener());
        receivedResponse = true;

        // poll another packet from the queue
        _sendPacket(PACKET_QUEUE.poll());
    }

    public synchronized void sendPacket(BufferedPacket packet) throws InvalidPacketFormatException, IOException {

        PACKET_QUEUE.add(packet);
        if (!receivedResponse) {
            return;
        }

        _sendPacket(PACKET_QUEUE.poll());
    }

    private synchronized void _sendPacket(BufferedPacket packet) throws IOException, IllegalStateException {
        if (!isInitializedStreams()) {
            throw new IllegalStateException("The I/O streams have not yet been initialized!");
        }
        if (packet == null) {
            return;
        }
        // ID
        out.write(PACKET_MAGIC.getBytes(StandardCharsets.UTF_8));
        out.write(format("%02x", packet.getPacketId()).getBytes(StandardCharsets.UTF_8));

        // Data length
        out.write(format("%03d", packet.getPacketSize()).getBytes(StandardCharsets.UTF_8));

        // Data
        out.write(packet.getData().getBytes(StandardCharsets.UTF_8));
        out.flush();
        receivedResponse = false;
        L.debug(format("Sending %s packet to the server...", packet));

    }

    /**
     * Sends a packet to the server.
     *
     * @param packet the packet to send
     * @throws IllegalStateException if the network manager has not yet been initialized
     */
    public synchronized void sendPacket(@NotNull PacketOut packet) throws IllegalStateException, AnnotationTypeMismatchException {
        // construct the packet based on annotated fields with PacketDataAnnotation
        // in the order of its value
        final TreeMap<Integer, StringBuilder> data = new TreeMap<>(Comparator.naturalOrder());
        for (Field field : packet.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(PacketDataField.class)) {
                continue;
            }
            final Object obj;
            try {
                obj = field.get(packet);
            } catch (IllegalAccessException e) {
                L.fatal(e);
                Platform.exit();
                return;
            }
            final int value = field.getAnnotation(PacketDataField.class).value();
            data.computeIfAbsent(value, x -> data.put(x, new StringBuilder()));

            final StringBuilder sb = data.get(value);
            final String s;
            if (obj instanceof String) {
                s = (String) obj;
            } else if (obj instanceof PacketData) {
                s = ((PacketData) obj).toDataString();
            } else {
                throw new AnnotationTypeMismatchException(null, obj.getClass().getSimpleName());
            }
            sb.append(s);
        }

        final StringBuilder sb = new StringBuilder();
        for (StringBuilder queueSb : data.values()) {
            sb.append(queueSb);
        }
        try {
            _sendPacket(new BufferedPacket(getState().getPacketId(packet.getClass()), sb.toString()));
        } catch (InvalidPacketFormatException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            L.fatal("Could not send a packet!", e);
        }
    }

    /**
     * Changes the current state of the client.
     *
     * @param state the state to change to
     */
    public void changeState(ProtocolState state) {
        this.state = state;
        L.info(format("Changing state to %s...", state.name()));
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
        L.debug("Closing both I/O streams...");
    }
}
