package jsmahy.ups_client.util;

import jsmahy.ups_client.net.PacketData;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A square on the board.
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public final class Square implements PacketData, Serializable {
	private final int rank, file;

	/**
	 * Instantiates a new position (square)
	 *
	 * @param rank the row
	 * @param file the column
	 *
	 * @throws IllegalArgumentException if either row or column are out of bounds
	 */
	public Square(int rank, int file) throws IllegalArgumentException {
		if (!isValidPosition(rank, file)) {
			throw new IllegalArgumentException(
					String.format(
							"Both parameters must be within the range of 0-7 (inclusive)! row=%d," +
									" column=%d", rank, file));
		}
		this.rank = rank;
		this.file = file;
	}

	/**
	 * Checks whether the rank and file are valid.
	 *
	 * @param rank the rank
	 * @param file the file
	 *
	 * @return {@code true} if both arguments are within 0-7 (inclusive) range
	 */
	public static boolean isValidPosition(int rank, int file) {
		// only the 3 lowest significant bits are needed
		return (rank >> 3) == 0 && (file >> 3) == 0;
	}

	/**
	 * Deserializes the squares from the data
	 *
	 * @param data       the data
	 * @param amountRead the amount read from the data
	 *
	 * @return the square
	 */
	public static Square deserialize(String data, AtomicInteger amountRead) {
		char file = data.charAt(0);
		if (file < 'A' || file > 'H') {
			throw new IllegalArgumentException(
					String.format("Invalid file in position (%c)", file));
		}
		char rank = data.charAt(1);
		if (rank < '1' || rank > '8') {
			throw new IllegalArgumentException(String.format("Invalid rank in position (%c)",
					rank));
		}
		amountRead.addAndGet(2);
		return new Square(rank - '1', file - 'A');
	}

	/**
	 * Flips the square coordinates on both axis, e.g. [1,4] (B5) -> [6,3] (G4)
	 *
	 * @return a flipped square
	 */
	public Square flip() {
		return new Square(7 - rank, 7 - file);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getRank(), getFile());
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
		return toDataString();
	}

	@Override
	public String toDataString() {
		return new String(new char[] {(char) (file + 'A'), (char) (rank + '1')});
	}

	/**
	 * @param rank the rank
	 * @param file the file
	 *
	 * @return a new position with the new coords
	 *
	 * @throws IllegalArgumentException if the new position is invalid
	 */
	public Square add(int rank, int file) throws IllegalArgumentException {
		return new Square(this.rank + rank, this.file + file);
	}
}
