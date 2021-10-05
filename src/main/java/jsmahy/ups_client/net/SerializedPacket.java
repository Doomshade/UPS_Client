package jsmahy.ups_client.net;

public class SerializedPacket {
    private final int packetId;
    private final String data;

    public SerializedPacket(int packetId, String data) {
        this.packetId = packetId;
        this.data = data;
    }

    public int getPacketId() {
        return packetId;
    }

    public String getData() {
        return data;
    }
}
