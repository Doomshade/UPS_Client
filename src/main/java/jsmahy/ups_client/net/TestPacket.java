package jsmahy.ups_client.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TestPacket extends AbstractPacket {
    public TestPacket() {
        super(0xff);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {

    }

    @Override
    public void read(DataInputStream in) throws IOException {

    }
}
