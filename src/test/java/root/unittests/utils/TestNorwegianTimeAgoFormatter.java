package root.unittests.utils;

import org.junit.jupiter.api.Test;
import root.includes.NorwegianTimeAgoTextFormatter;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class TestNorwegianTimeAgoFormatter {
    private String fmt(Instant instant) {
        String prefix = "for ";
        String suffix = " siden";

        return NorwegianTimeAgoTextFormatter.formatInstantAgo(instant, prefix, suffix);
    }

    @Test
    public void test() {

        assertEquals("-", fmt(null));
        assertEquals("for 10 sekunder siden", fmt(Instant.now().plus(Duration.ofSeconds(-10))));
        assertEquals("for 10 minutter siden", fmt(Instant.now().plus(Duration.ofMinutes(-10))));
        assertEquals("for 10 timer siden", fmt(Instant.now().plus(Duration.ofHours(-10))));
        assertEquals("for 4 dager siden", fmt(Instant.now().plus(Duration.ofDays(-4))));
        assertEquals("for 3 uker siden", fmt(Instant.now().plus(Duration.ofDays(-21))));
        assertEquals("for 4 måneder siden", fmt(Instant.now().plus(Duration.ofDays(-120))));
        assertEquals("for 4 år siden", fmt(Instant.now().plus(Duration.ofDays(-4 * 365))));
    }
}
