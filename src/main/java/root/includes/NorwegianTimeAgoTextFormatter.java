package root.includes;

/**
 * Formats a java.time.Instant into a human-readable "time ago" format in Norwegian.
 * Example: "for 5 minutter siden", "for 2 timer siden", etc.
 * Note: This is a simple implementation and may not cover all edge cases or localization nuances.
 * In part written by ChatGPT, with some adjustments for Norwegian language and pluralization.
 * TODO: Consider using a library like Joda-Time or java.time.format for more robust formatting and localization support.
 */

public class NorwegianTimeAgoTextFormatter {
    private static String[] names = { "sekund", "minutt", "time", "dag", "uke", "måned", "år" };
    private static String[] plurals = { "er", "er", "r", "er", "r", "er", "" };

    /**
     * Formats the given Instant into a "time ago" string in Norwegian.
     *
     * @param t
     * @param prefix
     * @param suffix
     * @return
     */

    public static String formatInstantAgo(java.time.Instant t, String prefix, String suffix) {
        if (t == null) return "-";

        int idx = 0;

        long s = java.time.Duration.between(t, java.time.Instant.now()).getSeconds();
        if (s < 60) return prefix + (s + " " + names[idx] + plurals[idx]) + suffix;
        idx++;

        long m = s / 60;
        if (m < 60) return prefix + (m + " " + names[idx] + plurals[idx]) + suffix;
        idx++;

        long h = m / 60;
        if (h < 24) return prefix + (h + " " + names[idx] + plurals[idx]) + suffix;
        idx++;

        long d = h / 24;
        if (d < 7) return prefix + (d + " " + names[idx] + plurals[idx]) + suffix;
        idx++;

        long w = d / 7;
        if (w < 4) return prefix + (w + " " + names[idx] + plurals[idx]) + suffix;
        idx++;

        long mo = d / 30;
        if (mo < 12) return prefix + (mo + " " + names[idx] + plurals[idx]) + suffix;
        idx++;

        long y = d / 365;
        return prefix + (y + " " + names[idx] + plurals[idx]) + suffix;
    }
}
