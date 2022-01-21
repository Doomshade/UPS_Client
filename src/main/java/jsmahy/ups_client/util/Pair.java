package jsmahy.ups_client.util;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @param <A>
 * @param <B>
 *
 * @author Jakub Šmrha
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
