package root.unittests.utils;

import org.junit.jupiter.api.Test;
import root.includes.PageCursor;
import root.includes.PageCursorEncoder;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.junit.jupiter.api.Assertions.*;

public class TestPageCursor {


    /**
     * Tests the parseOrDefault method of PageCursorEncoder. This method is responsible for parsing a string
     * representation of a page cursor, which consists of an offset and a limit, separated by a comma. The method should
     * return a PageCursor object with the parsed offset and limit values, or default values if the input string is
     * invalid.
     * <p>
     * quick tests to check some invalid ranges, then some valid ones. Used to develop code, not to test it very well.
     * Cursor is integral and is tested manually and by paginators.
     */

    @Test
    public void testParseOrDefault() {
        List<String> invalidStrings = List.of(
            "a,b",
            "a,50",
            "0,-1a",
            "50d,c"
        );

        // check invalid
        for (String invalidString : invalidStrings) {
            assertEquals(0, PageCursorEncoder.parseOrDefault(invalidString, Integer.MAX_VALUE).getOffset());
            assertEquals(Integer.MAX_VALUE, PageCursorEncoder.parseOrDefault(invalidString, Integer.MAX_VALUE).getLimit());
        }


        // check for correctness against pairs
        List<String> validStrings = List.of(
            "0",
            "10",
            "100,1",
            "1000,11",
            "10000,0"
        );
        List<Integer[]> validPairs = List.of(
            new Integer[]{0, Integer.MAX_VALUE},
            new Integer[]{0, Integer.MAX_VALUE},
            new Integer[]{100, 1},
            new Integer[]{1000, 11},
            new Integer[]{10000, 0}
        );

        for (int i = 0; i < validStrings.size(); i++) {
            String validString = validStrings.get(i);
            Integer[] validPair = validPairs.get(i);

            var c = PageCursorEncoder.parseOrDefault(validString, Integer.MAX_VALUE);

            //Logger.log("testing '" + validString + "' got offset " + c.getOffset() + " and limit " + c.getLimit() + ", validPair: " + Arrays.toString(validPair));

            assertEquals((int) validPair[0], c.getOffset());
            assertEquals((int) validPair[1], c.getLimit());
        }
    }

    @Test
    void testEncode(){
        assertEquals("0,0", new PageCursor(0, 0).encode());
        assertEquals("0,10", new PageCursor(0, 10).encode());
        assertEquals("0,0", new PageCursor(-5, 0).encode());
        assertEquals("10,100", new PageCursor(10,100).encode());
        assertEquals("100,10", new PageCursor(100,10).encode());
        assertEquals("0,0", new PageCursor(-100,-100).encode());

    }
}