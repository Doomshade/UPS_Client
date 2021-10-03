package jsmahy.ups_client.net;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable {
    private final String host;
    private final int port;
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void initializeConnection() throws IOException {
        this.socket = new Socket(host, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String sendPacket(Packet packet) throws IOException {
        out.println(packet.getMessage());
        return in.readLine();
    }

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
