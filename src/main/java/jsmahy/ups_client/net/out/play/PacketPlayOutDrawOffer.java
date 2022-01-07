package jsmahy.ups_client.net.out.play;

import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.out.PacketOut;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketPlayOutDrawOffer implements PacketOut {
    private final ResponseCode response;

    public PacketPlayOutDrawOffer(final ResponseCode response) {
        this.response = response;
    }

    @Override
    public void write(final BufferedOutputStream out) throws IOException {
        out.write(response.name().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("response", response)
                .toString();
    }
}
