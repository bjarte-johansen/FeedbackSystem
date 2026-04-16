package root.unittests.utils;

import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;
import root.includes.ImmutableUnboundedDateRange;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TestImmutableUnboundedDateRange {

    public ImmutableUnboundedDateRange<LocalDate> makeRange(LocalDate start, LocalDate end) {
        return new ImmutableUnboundedDateRange<LocalDate>(start, end);
    }

    @Test
    public void test_constructor(){
        ImmutableUnboundedDateRange<LocalDate> range;

        range = makeRange(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31));
        assertTrue(range.isOrdered());
        assertTrue(range.isValid());
        assertTrue(range.hasStart());
        assertTrue(range.hasEnd());
        assertTrue(range.isBounded());

        range = makeRange(null, LocalDate.of(2020, 12, 31));
        assertFalse(range.isOrdered());
        assertFalse(range.isValid());
        assertFalse(range.hasStart());
        assertTrue(range.hasEnd());
        assertFalse(range.isBounded());

        range = makeRange(LocalDate.of(2020, 1, 1), null);
        assertFalse(range.isOrdered());
        assertFalse(range.isValid());
        assertTrue(range.hasStart());
        assertFalse(range.hasEnd());
        assertFalse(range.isBounded());

        range = makeRange(null, null);
        assertFalse(range.isOrdered());
        assertFalse(range.isValid());
        assertFalse(range.hasStart());
        assertFalse(range.hasEnd());
        assertFalse(range.isBounded());
    }

    @Test
    public void test_ordering() {
        ImmutableUnboundedDateRange<LocalDate> range;

        range = makeRange(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31));
        assertTrue(range.isOrdered());

        assertThrows(IllegalArgumentException.class, () -> makeRange(LocalDate.of(2020, 12, 31), LocalDate.of(2020, 1, 1)));
        assertDoesNotThrow(() -> makeRange(LocalDate.of(2020, 12, 31), LocalDate.of(2021, 1, 1)));
    }

}
