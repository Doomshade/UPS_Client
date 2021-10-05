package jsmahy.ups_client.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public final class NetworkManager {
    private static final NetworkManager instance = new NetworkManager();
    private String host;
    private int port;
    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private static boolean successfullyInitialized = false;

    /**
     * Sets up (initializes) the network manager
     *
     * @param host the host
     * @param port the port
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

    private NetworkManager() {
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

    private void initializeConnection() throws IOException {
        this.socket = new Socket(host, port);
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());
        successfullyInitialized = true;
    }

    /**
     * Sends a packet
     *
     * @param packet the packet
     * @throws IOException the io exception
     */
    public String sendPacket(Packet packet) throws IOException {
        out.writeInt(packet.getId());
        packet.write(out);
        out.flush();
        return in.readUTF();
    }

    private String createHeader(Packet packet) {
        return String.valueOf(packet.getId());
    }
}
