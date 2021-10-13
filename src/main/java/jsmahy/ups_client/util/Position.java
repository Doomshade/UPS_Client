package jsmahy.ups_client.util;

import java.util.Objects;

/**
 * The type Position.
 */
public final class Position {
    private final byte row, column;

    public Position(Position other) {
        this(other.row, other.column);
    }

    /**
     * Instantiates a new position (square)
     *
     * @param row    the row
     * @param column the column
     *
     * @throws IllegalArgumentException if either row or column are out of bounds
     */
    public Position(byte row, byte column) throws IllegalArgumentException {
        validatePosition(row, column);
        this.row = row;
        this.column = column;
    }

    /**
     * Validates the position
     *
     * @param row    the row
     * @param column the column
     *
     * @throws IllegalArgumentException the illegal argument exception
     */
    public static void validatePosition(int row, int column) throws IllegalArgumentException {
        if (!isValidPosition(row, column)) {
            throw new IllegalArgumentException(
                    String.format(
                            "Both parameters must be within the range of 0-7 (inclusive)! row=%d," +
                                    " column=%d",
                            row,
                            column));
        }
    }

    public String toAsciiString() {
        return new String(new char[] {toChar(row), toChar(column)});
    }

    public static Position fromString(String s) throws IllegalArgumentException {
        if (s.length() != 2) {
            throw new IllegalArgumentException("The position length must be 2!");
        }
        char file = s.charAt(0);
        if (file < 'A' || file > 'H') {
            throw new IllegalArgumentException(
                    String.format("Invalid file in position (%c)", file));
        }
        char rank = s.charAt(1);
        if (rank < '0' || rank > '8') {
            throw new IllegalArgumentException(String.format("Invalid rank in position (%c)",
                    rank));
        }
        return new Position((byte) (file - 'A'), (byte) (rank - '0'));
    }

    private char toChar(int num) {
        return (char) (num + 'A');
    }

    /**
     * @param row    the row
     * @param column the column
     *
     * @return {@code true} if both arguments are within 0-7 (inclusive) range
     */
    public static boolean isValidPosition(int row, int column) {
        // only the 3 lowest significant bits are needed
        return (row >> 3) == 0 && (column >> 3) == 0;
    }

    /**
     * Encodes the position to a single short value
     *
     * @param from the position the piece moved from
     * @param to   the position the piece moved to
     *
     * @return
     */
    public static short encode(Position from, Position to) {
        return (short) ((from.getRow() << 9) | (from.getColumn() << 6) | (to.getRow() << 3) |
                to.getColumn());
    }

    /**
     * @return the row
     */
    public byte getRow() {
        return row;
    }

    /**
     * @return the column
     */
    public byte getColumn() {
        return column;
    }

    /**
     * Decodes a position
     *
     * @param position the position to decode
     *
     * @return a pair of positions; A = from, B = to
     */
    public static Pair<Position, Position> decode(short position) {
        byte fromX = (byte) ((position >> 9) & 0b111);
        byte fromY = (byte) ((position >> 6) & 0b111);
        byte toX = (byte) ((position >> 3) & 0b111);
        byte toY = (byte) (position & 0b111);

        Position from = new Position(fromX, fromY);
        Position to = new Position(toX, toY);

        return new Pair<>(from, to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getColumn());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Position position = (Position) o;
        return getRow() == position.getRow() && getColumn() == position.getColumn();
    }

    @Override
    public String toString() {
        return "Position{" +
                "row=" + row +
                ", column=" + column +
                '}';
    }
}
