package jsmahy.ups_client.net;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import jsmahy.ups_client.net.in.PacketIn;
import jsmahy.ups_client.net.out.PacketOut;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PacketDeserializer implements Runnable {
    private static final Logger L = LogManager.getLogger(PacketDeserializer.class);
    private static final NetworkManager NM = NetworkManager.getInstance();

    /**
     * The messages from server that are received by this input stream.
     */
    private final InputStream in;
    private final BufferedPacket bufferedPacket = new BufferedPacket();

    public PacketDeserializer(final InputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        while (true) {

            String s;
            int buffered;
            try {
                byte[] buf = new byte[1024];
                while (in.read(buf) == -1) {
                    Thread.onSpinWait();
                }
                s = new String(buf, StandardCharsets.UTF_8);
                s = s.trim();
                while (true) {
                    buffered = bufferedPacket.append(s);
                    // the data is fully buffered -> the packet is ready
                    // we can handle the packet now
                    if (bufferedPacket.isPacketReady()) {

                        // 0-0x7F = packet out, 0x80-0xFF = packet in
                        if (bufferedPacket.getPacketId() < ProtocolState.PACKET_IN_OFFSET) {
                            L.debug("Sending " + bufferedPacket);
                            NM.sendPacket(bufferedPacket);
                        } else {
                            L.debug("Received " + bufferedPacket);
                            NM.receivePacket(bufferedPacket);
                        }
                        bufferedPacket.reset();
                    } else {
                        break;
                    }
                    if (buffered < s.length()) {
                        try {
                            s = s.substring(buffered);
                        } catch (Exception e) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
            } catch (IOException | InvalidPacketFormatException e) {
                L.fatal(e);
                bufferedPacket.reset();
                // L.info("Disconnecting...");
                // break;
            }
        }
    }
}
