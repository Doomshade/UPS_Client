package jsmahy.ups_client.util;

import java.util.Objects;

/**
 * The type Position.
 */
public final class Position {
    private final int row, column;

    /**
     * Instantiates a new position (square)
     *
     * @param row    the row
     * @param column the column
     * @throws IllegalArgumentException if either row or column are out of bounds
     */
    public Position(int row, int column) throws IllegalArgumentException {
        validatePosition(row, column);
        this.row = row;
        this.column = column;
    }

    public Position(Position other) {
        this(other.row, other.column);
    }

    /**
     * Validates the position
     *
     * @param row    the row
     * @param column the column
     * @throws IllegalArgumentException the illegal argument exception
     */
    public static void validatePosition(int row, int column) throws IllegalArgumentException {
        if (!isValidPosition(row, column)) {
            throw new IllegalArgumentException(String.format("Both parameters must be within the range of 0-7 (inclusive)! row=%d, column=%d", row, column));
        }
    }

    /**
     * @param row    the row
     * @param column the column
     * @return {@code true} if both arguments are within 0-7 (inclusive) range
     */
    public static boolean isValidPosition(int row, int column) {
        return (row >> 3) == 0 && (column >> 3) == 0;
    }

    /**
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the column
     */
    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "Position{" +
                "row=" + row +
                ", column=" + column +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return getRow() == position.getRow() && getColumn() == position.getColumn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getColumn());
    }
}
