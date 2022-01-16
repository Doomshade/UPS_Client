package jsmahy.ups_client.net;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import jsmahy.ups_client.SceneManager;
import jsmahy.ups_client.exception.InvalidProtocolStateException;
import jsmahy.ups_client.net.in.PacketIn;
import jsmahy.ups_client.net.listener.PacketListener;
import jsmahy.ups_client.net.listener.impl.Client;
import jsmahy.ups_client.net.listener.impl.JustConnectedListener;
import jsmahy.ups_client.net.listener.impl.LoggedInListener;
import jsmahy.ups_client.net.listener.impl.QueueListener;
import jsmahy.ups_client.net.out.PacketOut;
import jsmahy.ups_client.util.AlertBuilder;
import jsmahy.ups_client.util.LoggableOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static java.lang.String.format;

/**
 * The network manager that handles packets and broadcasts them to listeners
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @see PacketListener
 * @see JustConnectedListener
 * @see Client
 * @since 1.0
 */
public final class NetworkManager {
	public static final String PACKET_MAGIC = "CHESS";
	public static final int PACKET_HEADER_LENGTH = PACKET_MAGIC.length() + 5;
	public static final int MAXIMUM_ID_LENGTH = 3;
	private static final NetworkManager INSTANCE = new NetworkManager();
	/**
	 * The timeout limit for server to answer.
	 */
	private static final int TIMEOUT = 30_000;
	private final Collection<Consumer<ProtocolState>> changedStateListeners = new ArrayList<>();
	private final Logger L = LogManager.getLogger(NetworkManager.class);
	/**
	 * The packet listener based on the protocol state.
	 */
	private final Map<ProtocolState, PacketListener> LISTENERS = new ConcurrentHashMap<>() {
		// test
		{
			put(ProtocolState.JUST_CONNECTED, new JustConnectedListener());
			put(ProtocolState.LOGGED_IN, new LoggedInListener());
			put(ProtocolState.QUEUE, new QueueListener());
		}
	};
	/**
	 * Indicates whether the connection was successfully instantiated.
	 */
	private boolean connectionSuccessful = false;
	private Socket socket = null;
	/**
	 * Messages from server are received by this input stream.
	 */
	private BufferedInputStream in = null;

	/**
	 * Messages to server are sent in this output stream.
	 */
	private BufferedOutputStream out = null;

	/**
	 * Indicates whether the I/O streams were initialized.
	 */
	private boolean initializedStreams = false;

	/**
	 * The current state of the protocol.
	 */
	private ProtocolState state = ProtocolState.JUST_CONNECTED;

	private NetworkManager() {
	}

	/**
	 * Sets up the packet listener in {@link ProtocolState#PLAY} state.
	 *
	 * @param listener the listener
	 */
	public static void setClient(@NotNull Client listener) {
		getInstance().LISTENERS.put(ProtocolState.PLAY, listener);
	}

	/**
	 * @return the instance
	 */
	public static NetworkManager getInstance() {
		return INSTANCE;
	}

	/**
	 * Sets up (initializes) the network manager.
	 *
	 * @param host the host
	 * @param port the port
	 *
	 * @throws IllegalStateException if the connection has already been established
	 */
	public void setup(@NotNull final String host, final int port, Runnable onError, Runnable onSuccess)
			throws IllegalStateException {
		if (isConnectionSuccessful()) {
			throw new IllegalStateException("Already connected to a server!");
		}
		L.info(format("Setting up Network Manager with host %s and port %d", host, port));
		new Thread(() -> {
			try {
				this.socket = new Socket(host, port);
				this.socket.setSoTimeout(TIMEOUT);
				this.socket.setKeepAlive(true);
				this.setupIO(socket.getInputStream(), socket.getOutputStream());
				connectionSuccessful = true;
				L.info("Successfully initialized connection");
				Platform.runLater(onSuccess);
			} catch (IOException e) {
				if (onError != null) {
					Platform.runLater(onError);
				}
			}
		}).start();
	}

	/**
	 * Sets up the I/O streams.
	 *
	 * @param in  the input stream
	 * @param out the output stream
	 *
	 * @throws IllegalStateException if the streams have already been initialized
	 */
	public void setupIO(@NotNull final InputStream in,
	                    @Nullable OutputStream out)
			throws IllegalStateException {
		if (isInitializedStreams()) {
			throw new IllegalStateException("I/O streams have already been initialized!");
		}
		L.info("Setting up I/O...");
		this.in = new BufferedInputStream(in);
		startListening(in);
		if (out == null) {
			try {
				File tempOut = Files.createTempFile("out", null).toFile();
				L.info("Created a new out temp file " + tempOut.getAbsolutePath());
				startListening(new FileInputStream(tempOut));
			} catch (IOException e) {
				L.fatal("Could not create a temp file", e);
				throw new RuntimeException(e);
			}
			L.info("Using simulated output stream...");
			//this.out = new BufferedOutputStream(System.out);
		} else {
			this.out = new BufferedOutputStream(new LoggableOutputStream(out));
		}
		this.initializedStreams = true;
		L.info("I/O set up");
	}

	private void startListening(final InputStream in) {
		Thread readThread = new Thread(new PacketDeserializer(in));
		readThread.setDaemon(true);
		readThread.start();
	}

	/**
	 * @return {@code true} if the I/O streams have been successfully initialized.
	 */
	public boolean isInitializedStreams() {
		return initializedStreams;
	}

	/**
	 * @return {@code true} if the connection has been successfully established.
	 */
	public boolean isConnectionSuccessful() {
		return connectionSuccessful;
	}

	public synchronized void receivePacket(BufferedPacket packet) throws IOException {
		receivePacket(getState().getPacketIn(packet.getPacketId()), packet.getData());
	}

	public synchronized void receivePacket(PacketIn p, String data) {
		final TreeMap<Integer, LinkedHashMap<Field, Object>> map = new TreeMap<>(Comparator.naturalOrder());
		final AtomicInteger amountRead = new AtomicInteger();

		// get all the fields annotated with packet data field
		// and register them to the tree map
		for (Field field : p.getClass().getDeclaredFields()) {
			if (!field.isAnnotationPresent(PacketDataField.class)) {
				continue;
			}

			int value = field.getAnnotation(PacketDataField.class).value();
			final Object obj;
			try {
				field.setAccessible(true);
				obj = field.get(p);
			} catch (IllegalAccessException e) {
				L.fatal(String.format("Could not access %s field in class %s!", field.getName(),
						p.getClass().getSimpleName()));
				Platform.exit();
				return;
			}

			if (!(obj instanceof PacketData || obj instanceof String || obj instanceof Boolean ||
					obj instanceof Number)) {
				throw new AnnotationTypeMismatchException(null, obj.getClass().getSimpleName());
			}
			map.computeIfAbsent(value, x -> map.put(x, new LinkedHashMap<>()));
			map.get(value).put(field, obj);
		}

		// then iterate through the fields and call their "deserialize" method
		for (Map<Field, Object> q : map.values()) {
			for (Map.Entry<Field, Object> entry : q.entrySet()) {
				final Field field = entry.getKey();
				field.setAccessible(true);
				final Object packetData = entry.getValue();
				final Method deserializeMethod;

				try {
					if (packetData instanceof Boolean) {
						// check if the start of the string is either a 1 or a 0
						field.setBoolean(p, data.substring(amountRead.get()).charAt(0) == '1');
						amountRead.incrementAndGet();
						continue;
					} else if (packetData instanceof String) {
						// get the whole data
						field.set(p, data.substring(amountRead.get()));
						amountRead.addAndGet(data.length());
						continue;
					} else if (packetData instanceof Integer) {
						// check for maximum id length bytes
						field.setInt(p, Integer.parseInt(
								data.substring(amountRead.get(), amountRead.get() + MAXIMUM_ID_LENGTH)));
						amountRead.addAndGet(MAXIMUM_ID_LENGTH);
						continue;
					}

					deserializeMethod =
							packetData.getClass().getDeclaredMethod("deserialize", String.class, AtomicInteger.class);
					System.out.print(field.get(p));
					System.out.print(" -> ");
					final Object invokedObj =
							deserializeMethod.invoke(null, data.substring(amountRead.get()), amountRead);
					System.out.println(invokedObj);
					field.set(p, invokedObj);
				} catch (IllegalAccessException | InvocationTargetException e) {
					L.fatal(String.format("Failed to invoke %s method in %s!", "deserialize",
							p.getClass().getSimpleName()), e);
					Platform.exit();
					return;
				} catch (NoSuchMethodException e) {
					L.fatal(String.format("%s does not implement %s method!",
							entry.getKey().getClass().getSimpleName(),
							"deserialize"));
					Platform.exit();
					return;
				}
			}
		}

		// now that the packet is deserialized we can handle it
		L.debug("Handling " + p);
		getCurrentListener().handle(p);
	}

	/**
	 * Gets the packet listener based on the current state.
	 *
	 * @return the packet listener
	 */
	public PacketListener getCurrentListener() {
		return getListener(state);
	}

	/**
	 * Gets the packet listener based on the given state.
	 *
	 * @param state the state
	 *
	 * @return the packet listener
	 */
	private PacketListener getListener(@NotNull ProtocolState state) {
		return LISTENERS.get(state);
	}

	/**
	 * @return current client state
	 */
	public ProtocolState getState() {
		return state;
	}

	public synchronized void sendPacket(PacketOut packet) {
		sendPacket(packet, null, null);
	}

	public synchronized void sendPacket(PacketOut packet, Runnable onFail, Runnable onSuccess) {
		sendPacket(packet, onFail, onSuccess, null, null);
	}

	/**
	 * Sends a packet to the server.
	 *
	 * @param packet the packet to send
	 *
	 * @throws IllegalStateException if the network manager has not yet been initialized
	 */
	public synchronized void sendPacket(PacketOut packet, Runnable onFail, Runnable onSuccess, Node node,
	                                    ProgressIndicator indicator)
			throws IllegalStateException, AnnotationTypeMismatchException, InvalidProtocolStateException {
		// construct the packet based on annotated fields with PacketDataAnnotation
		// in the order of its value
		final TreeMap<Integer, StringBuilder> data = new TreeMap<>(Comparator.naturalOrder());
		for (Field field : packet.getClass().getDeclaredFields()) {
			if (!field.isAnnotationPresent(PacketDataField.class)) {
				continue;
			}
			field.setAccessible(true);
			final Object obj;
			try {
				obj = field.get(packet);
			} catch (IllegalAccessException e) {
				L.fatal(e);
				Platform.exit();
				return;
			}
			final int value = field.getAnnotation(PacketDataField.class).value();
			data.computeIfAbsent(value, x -> data.put(x, new StringBuilder()));

			final StringBuilder sb = data.get(value);
			final String s;
			if (obj instanceof Boolean) {
				s = (Boolean) obj ? "1" : "0";
			} else if (obj instanceof String) {
				s = (String) obj;
			} else if (obj instanceof PacketData) {
				s = ((PacketData) obj).toDataString();
			} else if (obj instanceof Integer) {
				s = String.format("%d03", obj);
			} else {
				throw new AnnotationTypeMismatchException(null, obj.getClass().getSimpleName());
			}
			sb.append(s);
		}

		final StringBuilder sb = new StringBuilder();
		for (StringBuilder queueSb : data.values()) {
			sb.append(queueSb);
		}
		L.info(String.format("Sending packet %s (%s). Payload: %s", packet, packet.getClass().getSimpleName(),
				sb));
		sendPacket(new BufferedPacket(getState().getPacketId(packet.getClass()), sb.toString()), onFail, onSuccess,
				node, indicator);
	}

	public synchronized void sendPacket(BufferedPacket packet, final Runnable onFail, final Runnable onSuccess,
	                                    final Node node,
	                                    final ProgressIndicator indicator)
			throws IllegalStateException {
		if (!isInitializedStreams()) {
			throw new IllegalStateException("The I/O streams have not yet been initialized!");
		}
		if (packet == null) {
			return;
		}
		L.debug(format("Sending %s packet to the server...", packet));
		if (out == null) {
			L.error("No output stream specified, could not send a packet!");
			return;
		}

		new Thread(() -> {
			try {
				if (node != null && indicator != null) {
					Platform.runLater(() -> {
						node.setDisable(true);
						indicator.setProgress(-1);
						indicator.setDisable(false);
						indicator.setVisible(true);
					});
				}
				// CHESS
				out.write(PACKET_MAGIC.getBytes(StandardCharsets.UTF_8));

				// ID
				out.write(format("%02x", packet.getPacketId()).getBytes(StandardCharsets.UTF_8));

				// Data length
				out.write(format("%03d", packet.getPacketSize()).getBytes(StandardCharsets.UTF_8));

				// Data
				out.write(packet.getData().getBytes(StandardCharsets.UTF_8));
				out.flush();
				if (node != null && indicator != null) {
					Platform.runLater(() -> {
						node.setDisable(false);
						indicator.setDisable(true);
						indicator.setVisible(false);
					});
				}
				if (onSuccess != null) {
					Platform.runLater(onSuccess);
				}
				L.debug(format("Sent %s packet to the server...", packet));
				L.debug("Awaiting response from the server...");
			} catch (IOException e) {
				L.fatal("Failed to send a packet!", e);
				if (onFail != null) {
					Platform.runLater(onFail);
				}
				disconnect();
				Platform.runLater(() -> {
					new AlertBuilder(Alert.AlertType.INFORMATION)
							.title("Server shutdown")
							.header("Unable to reach server")
							.content("Packet was not able to reach the server!")
							.build()
							.show();
				});
			}
		}).start();
	}

	public void disconnect() {
		Client.logout();
		stopListening();
		changeState(ProtocolState.JUST_CONNECTED);
		SceneManager.changeScene(SceneManager.Scenes.SERVER_CONNECTION);
	}

	/**
	 * Changes the current state of the client.
	 *
	 * @param state the state to change to
	 */
	public void changeState(ProtocolState state) {
		this.state = state;
		L.info(format("Changing state to %s...", state.name()));
		for (Consumer<ProtocolState> listener : changedStateListeners) {
			listener.accept(state);
		}
	}

	/**
	 * Stops listening to the server - closes the socket and the I/O streams
	 */
	public void stopListening() {
		try {
			if (socket != null) {
				socket.close();
			}
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
		} catch (IOException ignored) {

		} finally {
			connectionSuccessful = false;
			initializedStreams = false;
			L.debug("Closing both I/O streams...");
		}

	}

	public void addChangedStateListener(Consumer<ProtocolState> listener) {
		changedStateListeners.add(listener);
	}

	public synchronized void sendPacket(BufferedPacket packet) throws IllegalStateException {
		sendPacket(packet, null, null);
	}

	public synchronized void sendPacket(BufferedPacket packet, Runnable onFail, Runnable onSuccess) {
		sendPacket(packet, onFail, onSuccess, null, null);
	}
}
