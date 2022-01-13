package jsmahy.ups_client.net.out;

public interface PacketData {
    String toDataString();

    PacketData fromDataString(String data);
}
