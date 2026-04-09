package root.includes;



/**
 * Represents a numeric range with a start and end value.
 * The start value should be less than or equal to the end value.
 */

@Deprecated
public record ImmutableNumericRange<T extends Number & Comparable<T>>(T start, T end) {
    public ImmutableNumericRange {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end values cannot be null.");
        }
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException("Start value must be less than or equal to end value.");
        }
    }
}
