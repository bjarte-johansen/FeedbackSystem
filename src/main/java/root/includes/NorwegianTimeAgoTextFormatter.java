package root.includes;

public class NorwegianTimeAgoTextFormatter {

    // TODO: Move this to a utility class if needed elsewhere
    // NOTE: written by chatGPT, may need adjustments for localization and edge cases

    public static String formatInstantAgo(java.time.Instant t, String prefix, String suffix) {
        if (t == null) return "";

        long s = java.time.Duration.between(t, java.time.Instant.now()).getSeconds();
        //if (s < 60) return prefix + s + " sek" + suffix;

        long m = s / 60;
        if (m < 60) return prefix + m + " minutt" + (m > 1 ? "er" : "") + suffix;

        long h = m / 60;
        if (h < 24) return prefix + h + " time" + (h > 1 ? "er" : "") + suffix;

        long d = h / 24;
        if (d < 7) return prefix + d + " dag" + (d > 1 ? "er" : "") + suffix;

        long w = d / 7;
        if (w < 4) return prefix + w + " uke" + (w > 1 ? "r" : "") + suffix;

        long mo = d / 30;
        if (mo < 12) return prefix + mo + " måned" + (mo > 1 ? "er" : "") + suffix;

        long y = d / 365;
        return prefix + y + " år" + suffix;
    }
}
