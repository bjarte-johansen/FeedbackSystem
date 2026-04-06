package root.includes;



/**
 * Represents a numeric range with a start and end value.
 * The start value should be less than or equal to the end value.
 */

@Deprecated
public class ImmutableNumericRange<T extends Number & Comparable<T>> {
    private final T start;
    private final T end;

    /**
     * The start value should be less than or equal to the end value.
     * @param start
     * @param end
     */
    public ImmutableNumericRange(T start, T end) {
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException("Start cannot be greater than end");
        }
        this.start = start;
        this.end = end;
    }

    /**
     * The start value should be less than or equal to the end value.
     * @return
     */
    public T getStart() {
        return start;
    }

    /**
     * The end value should be greater than or equal to the start value.
     * @return
     */
    public T getEnd() {
        return end;
    }
}
