package root.controllers;

import java.util.ArrayList;
import java.util.List;


/**
 * A utility class containing various helper methods. This class is not meant to be instantiated.
 * TODO: Consider splitting this class into multiple classes based on functionality (e.g., StringUtils, CollectionUtils)
 *  if it grows too large.
 */

public class LotsOfUtils {

    /**
     * @see #parseCsvIntList(String, String)
     */

    public static List<Integer> parseCsvIntList(String s) {
        return parseCsvIntList(s, ",");
    }


    /**
     * Parses a comma-separated string of integers into a list of integers. Example: "1, 2, 3" -> List.of(1, 2, 3) -
     * Handles null or empty input by returning an empty list. - Trims whitespace around numbers.
     *
     * @param s the comma-separated string to parse
     * @return a list of integers parsed from the input string
     * @throws NumberFormatException if any of the parts cannot be parsed as an integer
     * <p>
     * TODO: write unit test for it with various inputs, including edge cases (null, empty string, strings with
     *  extra spaces, invalid numbers)
     */

    public static List<Integer> parseCsvIntList(String s, String delimiter) {
        if (s == null || s.isEmpty()) return List.of();

        String[] parts = s.split(delimiter);

        List<Integer> result = new ArrayList<>(parts.length);
        for (String p : parts) {
            result.add(Integer.parseInt(p.trim()));
        }
        return result;
    }

}
