package jsmahy.ups_client.net.in.queue.packet;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.in.queue.PacketInQueue;
import jsmahy.ups_client.util.Util;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PacketQueueInGameStart implements PacketInQueue {
    @PacketDataField(1)
    private boolean white = true;
    @PacketDataField(2)
    private String fenString = Util.START_FEN;

    public String getFenString() {
        return fenString;
    }

    public boolean isWhite() {
        return white;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("white", white)
                .append("fenString", fenString)
                .toString();
    }
}
