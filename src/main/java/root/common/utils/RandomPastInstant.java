package root.common.utils;

/**
 * Utility class to generate a random Instant in the past within a specified range.
 * Example usage:
 *      Instant randomInstant = RandomPastInstant.generate(Duration.ofDays(1), Duration.ofDays(30));
 */

public class RandomPastInstant {
    /**
     * Generates a random Instant in the past between minAgo and maxAgo durations from now.
     * @param minAgo the minimum duration ago (e.g., Duration.ofDays(1) for at least 1 day ago)
     * @param maxAgo the maximum duration ago (e.g., Duration.ofDays(30) for at most 30 days ago)
     * @return a random Instant in the past between now - maxAgo and now - minAgo
     */
    public static java.time.Instant generate(java.time.Duration minAgo, java.time.Duration maxAgo) {
        long min = minAgo.getSeconds();
        long max = maxAgo.getSeconds();
        //if (min < 0 || max < min) throw new IllegalArgumentException();

        long delta = java.util.concurrent.ThreadLocalRandom.current().nextLong(min, max + 1);
        return java.time.Instant.now().minusSeconds(delta);
    }
}
