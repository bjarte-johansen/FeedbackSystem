package root.includes;

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
        checkArgument(min != null, "min cannot be null");
        checkArgument(max != null, "max cannot be null");
        checkArgument((min.compareTo(max)) <= 0, "max must be greater or equal to min");
    }


    /**
     * we immutable and constructor checks that min and max are valid, so we can assume that the range is always valid.
     * This method is provided for convenience and to allow for future validation logic if needed.
     *
     * @return true if the range is valid, false otherwise
     */

    public boolean isValid() {
        return true;
    }


    /**
     * Converts the Range object to a string with the specified delimiter (e.g. "1-10" if delimiter is "-").
     *
     * @return a comma-separated string representation of the range (e.g. "1,10")
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
     * Converts the Range object to a comma-separated string (e.g. "1,10").
     *
     * @return
     */

    @Override
    public String toString() {
        return min + "," + max;
    }
}