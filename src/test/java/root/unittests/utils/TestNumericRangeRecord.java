package root.unittests.utils;

import org.junit.jupiter.api.Test;
import root.includes.NumericRangeRecord;

import java.rmi.UnexpectedException;

import static org.junit.jupiter.api.Assertions.*;
import static root.common.utils.Preconditions.checkArgument;

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
}
