package jsmahy.ups_client.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * An output stream that logs the data sent before writing it.
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class LoggableOutputStream extends OutputStream {
	private static final Logger L = LogManager.getLogger(LoggableOutputStream.class);
	private final OutputStream out;

	public LoggableOutputStream(OutputStream out) {
		this.out = out;
	}

	@Override
	public void write(int b) throws IOException {
		L.debug(String.format("Writing %d to %s...", b, out.toString()));
		out.write(b);
	}

	@Override
	public void write(byte @NotNull [] b) throws IOException {
		L.debug(String.format("Writing %s to %s...", new String(b, StandardCharsets.UTF_8).trim(), out.toString()));
		out.write(b);
	}

	@Override
	public void write(byte @NotNull [] b, int off, int len) throws IOException {
		L.debug(String.format("Writing %s to %s (off=%d, len=%d)...", new String(b, StandardCharsets.UTF_8).trim(),
				out.toString(), off, len));
		out.write(b, off, len);
	}
}
