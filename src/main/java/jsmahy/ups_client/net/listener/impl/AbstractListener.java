package jsmahy.ups_client.net.listener.impl;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import jsmahy.ups_client.exception.InvalidProtocolStateException;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.in.PacketIn;
import jsmahy.ups_client.net.in.all.PacketInDisconnect;
import jsmahy.ups_client.net.in.all.PacketInInvalidData;
import jsmahy.ups_client.net.in.all.PacketInKeepAlive;
import jsmahy.ups_client.net.listener.PacketListener;
import jsmahy.ups_client.net.out.all.PacketOutKeepAlive;
import jsmahy.ups_client.util.AlertBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

abstract class AbstractListener implements PacketListener {
	/**
	 * The keepalive packet send period in millis
	 */
	public static final int KEEPALIVE_CHECK_PERIOD = 2_500;
	private static final Logger L = LogManager.getLogger(AbstractListener.class);

	// some keepalive variables
	private static long lastKeepAlive = -1;
	private static boolean sendingKeepAlive = false;
	private static Timer timer = null;

	// the handlers of each packet
	// the key is the packet class and the value is the handler
	private final Map<Class<? extends PacketIn>, Consumer<? extends PacketIn>> HANDLERS = new HashMap<>();

	{
		// global packets handled here
		register(PacketInKeepAlive.class, this::keepAlive);
		register(PacketInDisconnect.class, this::disconnect);
		register(PacketInInvalidData.class, this::invalidData);
	}

	/**
	 * Starts the keepalive packet sending
	 */
	protected static void startKeepAlive() {
		if (sendingKeepAlive) {
			return;
		}
		sendingKeepAlive = true;
		lastKeepAlive = -1;

		// the task that repeats every KEEPALIVE_CHECK_PERIOD
		final TimerTask task = new TimerTask() {

			@Override
			public void run() {
				// if the last keep alive is <=0 that means the task has just started
				if (lastKeepAlive <= 0) {
					lastKeepAlive = System.currentTimeMillis();
				}

				// the server hasn't responded in over MAX_TIMEOUT seconds, disconnect the client
				if (System.currentTimeMillis() - lastKeepAlive >= NetworkManager.MAX_TIMEOUT) {
					NetworkManager.getInstance().disconnect("Disconnected", "Could not reach the server",
							String.format("Reason: have not received a keep alive packet in %ds",
									NetworkManager.MAX_TIMEOUT / 1000), true);
					return;
				}

				// the server has responded, send another keepalive packet
				try {
					NetworkManager.getInstance().sendPacket(new PacketOutKeepAlive());
				} catch (Exception e) {
					L.error("Error sending packet");
					// stop the keepalive once an error occurs (the server is likely not responding)
					stopKeepAlive();
				}
			}
		};

		// set up the timer in a new daemon timer and schedule it
		timer = new Timer("keepAlive", true);
		timer.schedule(task, 0, KEEPALIVE_CHECK_PERIOD);
	}

	/**
	 * Stops sending keepalive packets
	 */
	public static void stopKeepAlive() {
		if (timer == null) {
			return;
		}
		sendingKeepAlive = false;
		lastKeepAlive = -1;
		timer.cancel();
		timer = null;
	}

	/**
	 * The invalid data packet handler
	 *
	 * @param packet the packet
	 */
	private void invalidData(final PacketInInvalidData packet) {
		Platform.runLater(() -> new AlertBuilder(Alert.AlertType.WARNING)
				.title("Invalid data")
				.header("The server received invalid data")
				.build().show());
	}

	/**
	 * The disconnect packet handler
	 *
	 * @param packet the packet
	 */
	private void disconnect(PacketInDisconnect packet) {
		NetworkManager.getInstance().disconnect("Disconnected", "Server sent a disconnect packet", "The server " +
				"disconnection reason: " + packet.getReason(), true);
	}

	/**
	 * The keepalive packet handler
	 *
	 * @param packet the packet
	 */
	private void keepAlive(final PacketInKeepAlive packet) {
		lastKeepAlive = System.currentTimeMillis();
	}

	/**
	 * Registers a handler for the given packet
	 *
	 * @param packetClass the packet
	 * @param handler     the handler
	 * @param <T>         the packet type
	 */
	protected final <T extends PacketIn> void register(Class<T> packetClass, Consumer<T> handler) {
		HANDLERS.put(packetClass, handler);
	}

	@Override
	public final <T extends PacketIn> void handle(T packet) throws InvalidProtocolStateException {
		final Consumer<PacketIn> handler = (Consumer<PacketIn>) HANDLERS.get(packet.getClass());
		if (handler == null) {
			throw new InvalidProtocolStateException("No handler found for " + packet.getClass().getSimpleName());
		}
		L.debug(String.format("Found handler for %s packet in class %s", packet, getClass().getSimpleName()));
		handler.accept(packet);
	}
}
