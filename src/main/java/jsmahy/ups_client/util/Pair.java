package jsmahy.ups_client.util;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A utility class for pairs
 *
 * @param <A> the first value
 * @param <B> the second value
 *
 * @author Jakub Šmrha
 * @version 1.0
 * @since 1.0
 */
public class Pair<A, B> {
	public final A a;
	public final B b;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("a", a)
				.append("b", b)
				.toString();
	}
}
