package jsmahy.ups_client.net.in.play.packet;

import jsmahy.ups_client.net.PacketDataField;
import jsmahy.ups_client.net.ResponseCode;
import jsmahy.ups_client.net.in.play.PacketInPlay;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * We moved a piece and now await confirmation.
 */
public class PacketPlayInMoveResponse implements PacketInPlay {
    // the order of the packets has to be like that because
    // the int value is a fixed size, whereas the response code isn't
    @PacketDataField(0)
    private int moveId = 0;

    @PacketDataField(1)
    private ResponseCode rc = ResponseCode.NONE;

    public ResponseCode getResponseCode() {
        return rc;
    }

    public int getMoveId() {
        return moveId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("moveId", moveId)
                .append("rc", rc)
                .toString();
    }
}
