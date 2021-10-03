package jsmahy.ups_client.util;

/**
 * The type Position.
 */
public final class Position {
    private final int x, y;

    /**
     * Instantiates a new Position.
     *
     * @param x the x
     * @param y the y
     * @throws IllegalArgumentException the illegal argument exception
     */
    public Position(int x, int y) throws IllegalArgumentException {
        validatePosition(x, y);
        this.x = x;
        this.y = y;
    }

    /**
     * Validate position.
     *
     * @param x the x
     * @param y the y
     * @throws IllegalArgumentException the illegal argument exception
     */
    public static void validatePosition(int x, int y) throws IllegalArgumentException {
        if (!((x >> 3) == 0 && (y >> 3) == 0)) {
            throw new IllegalArgumentException(String.format("Both parameters must be within the range of 0-7! x=%d, y=%d", x, y));
        }
    }

    /**
     * Gets x.
     *
     * @return the x
     */
    public int getX() {
        return x;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    /**
     * Gets y.
     *
     * @return the y
     */
    public int getY() {
        return y;
    }
}
