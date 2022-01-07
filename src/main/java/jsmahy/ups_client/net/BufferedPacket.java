package jsmahy.ups_client.net;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import org.apache.commons.lang3.builder.ToStringBuilder;

public final class BufferedPacket {

    private static final String PACKET_MAGIC = "CHESS";
    private static final int PACKET_HEADER_LENGTH = PACKET_MAGIC.length() + 5;
    private StringBuilder header = new StringBuilder(PACKET_HEADER_LENGTH);
    private StringBuilder data = new StringBuilder(1000);
    private int packetId = -1;
    private int packetSize = -1;

    public BufferedPacket() {
        reset();
    }

    public int append(String s) throws InvalidPacketFormatException, IllegalStateException {
        if (isPacketReady()) {
            return 0;
        }
        if (!isHeaderFull()) {
            int headerAppend = Math.min(s.length(), PACKET_HEADER_LENGTH - header.length());
            header.append(s, 0, headerAppend);
            if (!isHeaderFull()) {
                return headerAppend;
            }
            // the header is filled, parse it and continue
            parseHeader();
            return headerAppend + append(s.substring(headerAppend));
        }

        if (packetSize < 0) {
            throw new IllegalStateException("Packet size should not be <0!");
        }

        if (!isDataFull()) {
            int dataAppend = Math.min(s.length(), packetSize - data.length());
            data.append(s, 0, dataAppend);
            return dataAppend;
        }
        return 0;
    }

    private boolean isDataFull() {
        return data.length() == packetSize;
    }

    private boolean isHeaderFull() {
        return header.length() == PACKET_HEADER_LENGTH;
    }

    public boolean isPacketReady() {
        return packetId >= 0 && packetSize >= 0 && data.length() == packetSize;
    }

    private void parseHeader() throws InvalidPacketFormatException, IllegalStateException {
        if (!isHeaderFull()) {
            throw new IllegalStateException("Header not yet filled!");
        }
        try {
            packetId = Integer.parseInt(header.substring(PACKET_MAGIC.length(), PACKET_MAGIC.length() + 2), 16);
            packetSize = Integer.parseInt(header.substring(PACKET_MAGIC.length() + 2));
        } catch (NumberFormatException e) {
            throw new InvalidPacketFormatException(e);
        }
    }

    private void validatePacketReady() {
        if (!isPacketReady()) {
            throw new IllegalStateException("Packet not yet parsed!");

        }
    }

    public int getPacketId() {
        validatePacketReady();
        return packetId;
    }

    public int getPacketSize() {
        validatePacketReady();
        return packetSize;
    }

    public String getData() {
        validatePacketReady();
        return data.toString();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("header", header)
                .append("data", data)
                .append("packetId", packetId)
                .append("packetSize", packetSize)
                .toString();
    }

    public void reset() {
        packetId = -1;
        packetSize = -1;
        header = new StringBuilder(PACKET_HEADER_LENGTH);
        data = new StringBuilder(1000);
    }
}
