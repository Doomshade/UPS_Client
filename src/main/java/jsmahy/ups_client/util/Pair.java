package jsmahy.ups_client.util;

/**
 * The type Pair.
 *
 * @param <T> the type parameter
 * @param <V> the type parameter
 */
public class Pair<T, V> {
    /**
     * The T.
     */
    public final T t;
    /**
     * The V.
     */
    public final V v;

    /**
     * Instantiates a new Pair.
     *
     * @param t the t
     * @param v the v
     */
    public Pair(T t, V v) {
        this.t = t;
        this.v = v;
    }
}
