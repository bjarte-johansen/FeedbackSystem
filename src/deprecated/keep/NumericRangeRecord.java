package root.includes;

import java.lang.reflect.Array;
import java.util.Optional;
import java.util.function.Function;

import static root.common.utils.Preconditions.checkArgument;

/**
 * A simple generic class representing a numeric range with minimum and maximum values. It provides methods to convert
 * the range to a comma-separated string and to parse a comma-separated string into a Range object.
 * <p>
 * IMPORTANT: the class will happily accept "1", "1,2", "1-2" and [1] and [1,2] as valid inputs, if only one value
 * exists it will be duplicated to create a range where min and max are the same. IMPORTANT: No validation is done. It
 * is the responsibility of the caller to ensure that the input is valid.
 */

public record NumericRangeRecord<T extends Number & Comparable<? super T>>(T min, T max) {

    public NumericRangeRecord {
        checkArgument(isValid(), "min and max must be non-null, and min must be less than or equal to max");
    }


    /**
     * checks that the min and max values are not null and min is less than or equal to max. Otherwise, it returns
     * false.
     *
     * @return true if the range is valid, false otherwise
     */

    public boolean isValid() {
        return (min != null) && (max != null) && (min.compareTo(max) <= 0);
    }


    /**
     * Converts the Range object to a comma-separated string (e.g. "1,10").
     *
     * @return
     */

    public String toCSV() {
        return toCSV(",");
    }


    /**
     * Converts the Range object to a string with the specified delimiter (e.g. "1-10" if delimiter is "-").
     *
     * @param delimiter
     * @return
     */

    public String toCSV(String delimiter) {
        checkArgument(delimiter != null, "delimiter cannot be null");

        return min + delimiter + max;
    }


    /**
     * Converts the Range object to an array of two elements, where the first element is the minimum value and the
     * second element is the maximum value. The type of the array is determined by the type of the minimum and maximum
     * values.
     *
     * @param array
     * @return
     */

    @SuppressWarnings("unchecked")
    public T[] toArray(T[] array) {
        checkArgument(array != null, "array cannot be null");

        if (array.length >= 2) {
            array[0] = min;
            array[1] = max;
            return array;
        }

        T[] result = (T[]) Array.newInstance(array.getClass().getComponentType(), 2);
        result[0] = min;
        result[1] = max;
        return result;
    }


    /**
     * Converts the Range object to a comma-separated string (e.g. "1,10").
     *
     * @return
     */

    @Override
    public String toString() {
        return min + "," + max;
    }


    /**
     * Parses an array of two elements into a Range object. If the input array has only one element, it will be treated
     * as a range where min and max are the same (e.g. [5] will be treated as [5,5]). If the input array has more than
     * two elements, an IllegalArgumentException will be thrown.
     * <p>
     * If strict is set, the method will throw an IllegalArgumentException if the input array is null or does not
     * contain exactly two elements. If strict is not set, the method will return an empty Optional in these cases
     * instead of throwing an exception.
     *
     * @param input
     * @param <T>
     * @return
     * @throws IllegalArgumentException
     */

    public static <T extends Number & Comparable<? super T>> Optional<NumericRangeRecord<T>> fromArray(T[] input, boolean strict) throws IllegalArgumentException {
        checkArgument(
            !(strict && (input == null || input.length != 2)),
            "Input array must be valid and have exactly 2 elements when strict mode is enabled"
        );

        return (input == null || input.length == 0)
            ? Optional.empty()
            : Optional.of(new NumericRangeRecord<>(input[0], (input.length == 2) ? input[1] : input[0]));
    }


    /**
     * Parses a comma-separated string (e.g. "1,10") into a Range object. If the input is a single value (e.g. "5"), it
     * will be treated as a range where min and max are the same (e.g. "5,5").
     * <p>
     * If strict is set, the method will throw an IllegalArgumentException if the input string is null, empty, or does
     * not contain exactly two values separated by the specified delimiter. If strict is not set, the method will return
     * an empty Optional in these cases instead of throwing an exception.
     *
     * @param csv
     * @param parser
     * @param <R>
     * @return
     * @throws IllegalArgumentException
     */

    public static <R extends Number & Comparable<? super R>> Optional<NumericRangeRecord<R>> fromCSV(String csv, Function<String, R> parser, String delimiter, boolean strict) throws IllegalArgumentException {
        checkArgument(delimiter != null, "delimiter cannot be null");

        if (csv == null) {
            checkArgument(!strict, "csv cannot be null or empty when strict mode is enabled");
            return Optional.empty();
        }

        String[] parts = csv.split(delimiter);

        checkArgument(parts.length == 2 || (!strict && parts.length == 1), "Input string must be one or two values (strict = exactly two) of numeric type, separated by delimiter");

        try {
            R left = parser.apply(parts[0].trim());
            R right = (parts.length == 2) ? parser.apply(parts[1].trim()) : left;
            return Optional.of(new NumericRangeRecord<>(left, right));
        } catch (RuntimeException  e) {
            throw new IllegalArgumentException("Input string must be one or two values (strict = exactly two) of numeric type, separated by delimiter", e);
        }
    }
}