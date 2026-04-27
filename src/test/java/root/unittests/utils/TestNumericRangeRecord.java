package root.unittests.utils;

import org.junit.jupiter.api.Test;
import root.includes.NumericRangeRecord;

import java.rmi.UnexpectedException;

import static com.google.common.base.Preconditions.checkArgument;
import static org.junit.jupiter.api.Assertions.*;

public class TestNumericRangeRecord {

    public NumericRangeRecord<Integer> makeRange(Integer start, Integer end) {
        var tmp = new NumericRangeRecord<Integer>(start, end);
        checkArgument(tmp.start() != null, "min cannot be null");
        checkArgument(tmp.end() != null, "max cannot be null");
        checkArgument((tmp.start().compareTo(tmp.end())) <= 0, "max must be greater or equal to min");
        return tmp;
    }


    /**
     * uses makeRange to create object and check that min/max is the same as parameters
     */

    @Test
    public void testNumericRangeRecord() {
        assertDoesNotThrow(() -> makeRange(1, 10));
        assertDoesNotThrow(() -> makeRange(-100, 100));
        //assertDoesNotThrow(() -> makeRange(0.5, 1.5));
        assertDoesNotThrow(() -> makeRange(1, 1));

        String a = "test";
        Object o = (Object) a;

        assertThrows(IllegalArgumentException.class, () -> makeRange(1, -1));
        assertThrows(IllegalArgumentException.class, () -> makeRange(100, 10));
        assertThrows(IllegalArgumentException.class, () -> makeRange(1, 0));
        assertThrows(IllegalArgumentException.class, () -> makeRange(null, 0));
        assertThrows(IllegalArgumentException.class, () -> makeRange(1, null));
        assertThrows(IllegalArgumentException.class, () -> makeRange(null, null));
        assertThrows(ClassCastException.class, () -> makeRange((Integer) o, null));
    }

    /**
     * Her er noen tester laget av chatGPT som tok en copy-paste av klassen vår. Dette vil være veldig
     * greit når man utvider funksjonalitet og bruker TDD for mindre klasser i fremtiden
     */

    @Test
    void validRange_shouldCreate() {
        var r = new NumericRangeRecord<>(1, 5);

        assertEquals(1, r.start());
        assertEquals(5, r.end());
        assertTrue(r.isValid());
    }

    @Test
    void equalValues_shouldBeAllowed() {
        var r = new NumericRangeRecord<>(3, 3);

        assertEquals(3, r.start());
        assertEquals(3, r.end());
    }

    @Test
    void startGreaterThanEnd_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
            () -> new NumericRangeRecord<>(5, 1));
    }

    @Test
    void nullStart_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
            () -> new NumericRangeRecord<Integer>(null, 1));
    }

    @Test
    void nullEnd_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
            () -> new NumericRangeRecord<>(1, null));
    }

    @Test
    void toCSV_defaultDelimiter() {
        var r = new NumericRangeRecord<>(1, 10);

        assertEquals("1,10", r.toCSV());
    }

    @Test
    void toCSV_customDelimiter() {
        var r = new NumericRangeRecord<>(1, 10);

        assertEquals("1-10", r.toCSV("-"));
    }

    @Test
    void toCSV_nullDelimiter_shouldThrow() {
        var r = new NumericRangeRecord<>(1, 10);

        assertThrows(IllegalArgumentException.class,
            () -> r.toCSV(null));
    }

    @Test
    void toString_shouldMatchCSV() {
        var r = new NumericRangeRecord<>(2, 8);

        assertEquals("2,8", r.toString());
    }

    @Test
    void worksWithDifferentNumberTypes() {
        var r = new NumericRangeRecord<>(1.5, 2.5);

        assertEquals("1.5,2.5", r.toCSV());
    }
}