package jsmahy.ups_client.net.out.logged_in;

import jsmahy.ups_client.net.out.PacketOut;

import java.io.IOException;
import java.io.OutputStream;

public class PacketLoggedInOutJoinQueue implements PacketOut {
    public static Object[] deserializeParams(String data) {
        return null;
    }

    @Override
    public void write(OutputStream out) throws IOException {
        // TODO
    }
}
