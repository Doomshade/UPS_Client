package jsmahy.ups_client.net;

import jsmahy.ups_client.net.in.LobbyListener;
import jsmahy.ups_client.net.in.PacketIn;
import jsmahy.ups_client.net.in.PacketListener;
import jsmahy.ups_client.net.in.PacketListenerPlay;
import jsmahy.ups_client.net.out.PacketOut;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public final class NetworkManager {
    private static final NetworkManager instance = new NetworkManager();
    private static final Map<ProtocolState, PacketListener> LISTENERS = new HashMap<>() {
        {
            put(ProtocolState.LOBBY, new LobbyListener());
        }
    };
    private static boolean successfullyInitialized = false;

    private String host;
    private int port;
    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private ProtocolState state = ProtocolState.LOBBY;



    private NetworkManager() {
    }

    /**
     * Sets up (initializes) the network manager
     *
     * @param host the host
     * @param port the port
     *
     * @throws IOException if the connection could not be established
     */
    public static void setup(String host, int port) throws IOException {
        instance._setup(host, port);
    }

    private void _setup(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        initializeConnection();
    }

    private void initializeConnection() throws IOException {
        this.socket = new Socket(host, port);
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());
        this.socket.setSoTimeout(30_000);
        this.socket.setKeepAlive(true);

        final Runnable read = () -> {
            while (true) {
                try {
                    final int packetId = in.readUnsignedByte();
                    final int state = in.readUnsignedByte();

                    final ProtocolState protocolState = ProtocolState.getById(state);
                    if (NetworkManager.this.state != protocolState) {
                        in.skipBytes(in.available());
                        return;
                    }
                    final PacketIn<? extends PacketListener> packet =
                            (PacketIn<? extends PacketListener>) protocolState.getPacket(PacketDirection.CLIENT_BOUND,
                                    packetId);
                    packet.broadcast(getCurrentListener());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Thread readThread = new Thread(read);
        readThread.setDaemon(true);
        readThread.start();


        successfullyInitialized = true;
    }

    private <T extends PacketListener> T getCurrentListener() {
        return (T) getListener(state);
    }

    private PacketListener getListener(ProtocolState state) {
        return LISTENERS.get(state);
    }

    public void sendPacket(PacketOut packet) {
        try {
            final int id = state.getPacketId(PacketDirection.SERVER_BOUND, packet.getClass());
            // Packet format: [ID | State | Data]
            // ID
            out.writeByte(id);
            // State
            out.writeByte(state.ordinal());
            // Data
            packet.write(out);

            // write
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setListener(PacketListenerPlay listener) {
        LISTENERS.put(ProtocolState.PLAY, listener);
    }

    public static NetworkManager getInstance() {
        return instance;
    }

    /**
     * @return {@code true} whether the network manager has been successfully initialized
     */
    public static boolean isSuccessfullyInitialized() {
        return successfullyInitialized;
    }

    public void changeState(ProtocolState state) {
        this.state = state;
    }

    public ProtocolState getState() {
        return state;
    }

    public void stopListening() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
