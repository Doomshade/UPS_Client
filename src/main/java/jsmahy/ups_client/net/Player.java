package jsmahy.ups_client.net;

import java.io.*;
import java.net.Socket;

/**
 * The type Player.
 */
public class Player implements Runnable {
    private final String host;
    private final int port;
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;

    /**
     * Instantiates a new Player.
     *
     * @param host the host
     * @param port the port
     */
    public Player(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void initializeConnection() throws IOException {
        this.socket = new Socket(host, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Send packet.
     *
     * @param packet the packet
     * @throws IOException the io exception
     */
    public void sendPacket(Packet packet) throws IOException {
        out.println(packet.getMessage());
    }

    /**
     * Disconnect.
     *
     * @throws IOException the io exception
     */
    public void disconnect() throws IOException {
        out.close();
        socket.close();
    }

    @Override
    public void run() {
        try {
            initializeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
