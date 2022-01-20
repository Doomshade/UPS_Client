package jsmahy.ups_client.net.listener.impl;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import jsmahy.ups_client.net.NetworkManager;
import jsmahy.ups_client.net.ProtocolState;
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
	public static final int SERVER_RESPONSE_LIMIT = 30_000;
	public static final int KEEPALIVE_CHECK_PERIOD = 5_000;
	private static final Logger L = LogManager.getLogger(AbstractListener.class);
	private static long lastKeepAlive = 0;
	private static boolean sendingKeepAlive = false;
	private static Timer timer = null;
	private final Map<Class<? extends PacketIn>, Consumer<? extends PacketIn>> MAP = new HashMap<>();

	{
		register(PacketInKeepAlive.class, this::keepAlive);
		register(PacketInDisconnect.class, this::disconnect);
		register(PacketInInvalidData.class, this::invalidData);
	}

	protected static void startKeepAlive() {
		final NetworkManager NM = NetworkManager.getInstance();
		if (sendingKeepAlive || NM.getState() == ProtocolState.JUST_CONNECTED) {
			return;
		}
		sendingKeepAlive = true;
		final TimerTask task = new TimerTask() {

			@Override
			public void run() {
				if (lastKeepAlive <= 0) {
					lastKeepAlive = System.currentTimeMillis();
				}
				// the server hasn't responded in over 30 seconds, disconnect the client
				if (System.currentTimeMillis() - lastKeepAlive >= SERVER_RESPONSE_LIMIT) {
					NM.disconnect("Disconnected", "Could not reach the server",
							String.format("Reason: have not received a keep alive packet in %ds",
									SERVER_RESPONSE_LIMIT / 1000));
					return;
				}
				try {
					NM.sendPacket(new PacketOutKeepAlive());
				} catch (Exception e) {
					L.error("Error sending packet", e);
					stopKeepAlive();
				}
			}
		};
		timer = new Timer("keepAlive", true);
		timer.schedule(task, 0, KEEPALIVE_CHECK_PERIOD);
	}

	public static void stopKeepAlive() {
		if (timer == null) {
			return;
		}
		sendingKeepAlive = false;
		timer.cancel();
		timer = null;
	}

	private void invalidData(final PacketInInvalidData packet) {
		Platform.runLater(() -> new AlertBuilder(Alert.AlertType.WARNING)
				.title("Invalid data")
				.header("The server received invalid data")
				.content("The data: " + packet.getInvalidData())
				.build().show());
	}

	private void disconnect(PacketInDisconnect packet) {
		NetworkManager.getInstance().disconnect("Disconnected", "Server sent a disconnect packet", "The server " +
				"disconnection reason: " + packet.getReason());
	}

	private void keepAlive(final PacketInKeepAlive packet) {
		lastKeepAlive = System.currentTimeMillis();
	}

	protected final <T extends PacketIn> void register(Class<T> packetClass, Consumer<T> handler) {
		MAP.put(packetClass, handler);
	}

	@Override
	public final void handle(PacketIn packet) {
		final Consumer<PacketIn> handler = (Consumer<PacketIn>) MAP.get(packet.getClass());
		if (handler == null) {
			throw new IllegalStateException("No handler found for " + packet.getClass().getSimpleName());
		}
		L.debug(String.format("Found handler for %s packet in class %s", packet, getClass().getSimpleName()));
		handler.accept(packet);
	}
}
