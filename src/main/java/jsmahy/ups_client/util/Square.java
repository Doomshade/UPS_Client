package jsmahy.ups_client.util;

import jsmahy.ups_client.net.out.PacketData;

import java.util.Objects;

public final class Square implements PacketData {
    private int rank, file;

    public Square() {
        this.rank = -1;
        this.file = -1;
    }

    public Square(Square other) {
        this(other.rank, other.file);
    }

    /**
     * Instantiates a new position (square)
     *
     * @param rank the row
     * @param file the column
     * @throws IllegalArgumentException if either row or column are out of bounds
     */
    public Square(int rank, int file) throws IllegalArgumentException {
        ChessPieceUtil.validatePosition(rank, file);
        this.rank = rank;
        this.file = file;
    }

    /**
     * Checks whether the rank and file are valid.
     *
     * @param rank the rank
     * @param file the file
     * @return {@code true} if both arguments are within 0-7 (inclusive) range
     */
    public static boolean isValidPosition(int rank, int file) {
        // only the 3 lowest significant bits are needed
        return (rank >> 3) == 0 && (file >> 3) == 0;
    }

    public static Square fromString(String s) throws IllegalArgumentException {
        if (s.length() != 2) {
            throw new IllegalArgumentException(String.format("Invalid position %s! The position " +
                    "length " +
                    "must be 2!", s));
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
        return new Square(file - 'A', rank - '0');
    }

    /**
     * @return the row
     */
    public int getRank() {
        return rank;
    }

    /**
     * @return the column
     */
    public int getFile() {
        return file;
    }

    public String toAsciiString() {
        return new String(new char[]{toChar(rank), toChar(file)});
    }

    private char toChar(int num) {
        return (char) (num + 'A');
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRank(), getFile());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Square square = (Square) o;
        return getRank() == square.getRank() && getFile() == square.getFile();
    }

    @Override
    public String toString() {
        return "Position{" +
                "row=" + rank +
                ", column=" + file +
                '}';
    }

    /**
     * @param rank the rank
     * @param file the file
     * @return a new position with the new coords
     * @throws IllegalArgumentException if the new position is invalid
     */
    public Square add(int rank, int file) throws IllegalArgumentException {
        return new Square(this.rank + rank, this.file + file);
    }

    @Override
    public String toDataString() {
        return new String(new char[]{toChar(rank), toChar(file)});
    }

    @Override
    public PacketData fromDataString(String data) {
        return fromString(data);
    }
}
