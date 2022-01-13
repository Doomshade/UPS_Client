package jsmahy.ups_client.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class LoggableOutputStream extends OutputStream {
    private static final Logger L = LogManager.getLogger(LoggableOutputStream.class);
    private final OutputStream out;

    public LoggableOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        L.debug(String.format("Writing %d to %s...%n", b, out.toString()));
        out.write(b);
    }

    @Override
    public void write(byte @NotNull [] b) throws IOException {
        L.debug(String.format("Writing %s to %s...%n", new String(b, StandardCharsets.UTF_8), out.toString()));
        out.write(b);
    }

    @Override
    public void write(byte @NotNull [] b, int off, int len) throws IOException {
        L.debug(String.format("Writing %s to %s (off=%d, len=%d)...%n", new String(b, StandardCharsets.UTF_8), out.toString(), off, len));
        out.write(b, off, len);
    }
}
