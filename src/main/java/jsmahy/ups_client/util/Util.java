package jsmahy.ups_client.util;

import java.util.regex.Pattern;

public class Util {
    /**
     * The starting position.
     */
    public static final String START_FEN =
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    /**
     * The FEN pattern.
     */
    public static final Pattern FEN_PATTERN =
            Pattern.compile(
                    "((([rnbqkpRNBQKP1-8]+)\\/){7}([rnbqkpRNBQKP1-8]+)) ([wb]) (K?Q?k?q?|\\-) (" +
                            "([a-hA-H][0-7])|\\-) (\\d+) (\\d+)");
}
