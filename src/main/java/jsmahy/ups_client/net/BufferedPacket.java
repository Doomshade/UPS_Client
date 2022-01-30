package jsmahy.ups_client.net;

import jsmahy.ups_client.exception.InvalidPacketFormatException;
import org.apache.commons.lang3.builder.ToStringBuilder;

public final class BufferedPacket implements Cloneable {

	// the packet header
	private StringBuilder header;

	// the packet data
	private StringBuilder data;

	// the deserialized packet ID
	private int packetId;

	// the deserialized packet size
	private int packetSize;

	public BufferedPacket(int packetId, String data) throws InvalidPacketFormatException, IllegalStateException {
		this();
		this.packetId = packetId;
		this.packetSize = data.length();
		this.data.append(data);
	}

	public BufferedPacket() {
		reset();
	}

	/**
	 * Resets the packet's state
	 */
	public synchronized void reset() {
		packetId = -1;
		packetSize = -1;
		header = new StringBuilder(NetworkManager.PACKET_HEADER_LENGTH);
		data = new StringBuilder(1000);
	}

	/**
	 * Appends a string to the packet
	 *
	 * @param s the string
	 *
	 * @return the amount appended
	 *
	 * @throws InvalidPacketFormatException if the packet header is invalid
	 * @throws IllegalStateException        if the packet size is invalid
	 */
	public synchronized int append(String s) throws InvalidPacketFormatException, IllegalStateException {
		if (isPacketReady()) {
			return 0;
		}
		if (isHeaderEmpty()) {
			int headerAppend = Math.min(s.length(), NetworkManager.PACKET_HEADER_LENGTH - header.length());
			header.append(s, 0, headerAppend);
			if (isHeaderEmpty()) {
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

	private boolean isHeaderEmpty() {
		return header.length() != NetworkManager.PACKET_HEADER_LENGTH;
	}

	/**
	 * Parses the header
	 *
	 * @throws InvalidPacketFormatException if the header is invalid
	 * @throws IllegalStateException        if the header is not yet full
	 */
	private void parseHeader() throws InvalidPacketFormatException, IllegalStateException {
		if (isHeaderEmpty()) {
			throw new IllegalStateException("Header not yet filled!");
		}

		// parse packet magic
		String packetMagic = header.substring(0, NetworkManager.PACKET_MAGIC.length());
		if (!packetMagic.equals(NetworkManager.PACKET_MAGIC)) {
			throw new InvalidPacketFormatException("Invalid packet magic!");
		}

		// parse the packet header
		try {
			packetId = Integer.parseInt(
					header.substring(NetworkManager.PACKET_MAGIC.length(), NetworkManager.PACKET_MAGIC.length() + 2),
					16);
			packetSize = Integer.parseInt(header.substring(NetworkManager.PACKET_MAGIC.length() + 2));
		} catch (NumberFormatException e) {
			throw new InvalidPacketFormatException("Invalid packet header!", e);
		}
	}

	/**
	 * @return the packet ID
	 *
	 * @throws IllegalStateException if the packet is not ready yet
	 */
	public int getPacketId() throws IllegalStateException {
		checkIfPacketReady();
		return packetId;
	}

	/**
	 * Checks if the packet is ready
	 *
	 * @throws IllegalStateException if the packet is not ready
	 */
	private void checkIfPacketReady() throws IllegalStateException {
		if (!isPacketReady()) {
			throw new IllegalStateException("Packet not yet parsed! (" + this + ")");
		}
	}

	/**
	 * Checks whether the ID is >=0, packet size is >=0, and the data length matches the packet size
	 *
	 * @return {@code true} if the conditions are met
	 */
	public synchronized boolean isPacketReady() {
		return packetId >= 0 && packetSize >= 0 && isDataFull();
	}

	private boolean isDataFull() {
		return data.length() == packetSize;
	}

	/**
	 * @return the packet size
	 *
	 * @throws IllegalStateException if the packet is not ready yet
	 */
	public int getPacketSize() throws IllegalStateException {
		checkIfPacketReady();
		return packetSize;
	}

	/**
	 * @return the payload
	 *
	 * @throws IllegalStateException if the packet is not ready yet
	 */
	public String getData() throws IllegalStateException {
		checkIfPacketReady();
		return data.toString();
	}

	@Override
	public BufferedPacket clone() throws CloneNotSupportedException {
		BufferedPacket clone = (BufferedPacket) super.clone();
		clone.header = header;
		clone.data = data;
		clone.packetSize = packetSize;
		clone.packetId = packetId;

		return clone;
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
}
