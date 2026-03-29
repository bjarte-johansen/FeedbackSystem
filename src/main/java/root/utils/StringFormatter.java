package root.utils;


/**
 * written by ChatGPT, modified by Bjarte Johansen, 2026
 */

public class StringFormatter {
    public static String format(String fmt, Object... args) {
        return stringf(fmt, args);
    }

    public static String stringf(String fmt, Object... args) {
        int len = fmt.length();
        StringBuilder sb = new StringBuilder(len + (args.length << 4));

        int ai = 0;

        for (int i = 0; i < len; i++) {
            char c = fmt.charAt(i);

            if (c != '%' || i + 1 >= len) {
                sb.append(c);
                continue;
            }

            char t = fmt.charAt(++i);

            // %%
            if (t == '%') {
                sb.append('%');
                continue;
            }

            if (ai >= args.length) {
                sb.append('%').append(t);
                continue;
            }

            Object a = args[ai++];

            // --- parse optional width / precision (only for %f) ---
            int precision = -1;

            if (t == '.') {
                int p = 0;
                boolean has = false;

                while (i + 1 < len) {
                    char d = fmt.charAt(i + 1);
                    if (d >= '0' && d <= '9') {
                        has = true;
                        p = p * 10 + (d - '0');
                        i++;
                    } else break;
                }

                precision = has ? p : -1;

                if (i + 1 < len) {
                    t = fmt.charAt(++i);
                }
            }

            switch (t) {
                case 's':
                    sb.append(a == null ? "null" : a);
                    break;

                case 'd':
                    if (a instanceof Number n)
                        sb.append(n.longValue());
                    else
                        sb.append(a);
                    break;

                case 'f':
                    if (a instanceof Number n) {
                        double v = n.doubleValue();
                        if (precision >= 0) {
                            appendDouble(sb, v, precision);
                        } else {
                            sb.append(v);
                        }
                    } else {
                        sb.append(a);
                    }
                    break;

                default:
                    sb.append('%').append(t);
            }
        }

        return sb.toString();
    }

    private static void appendDouble(StringBuilder sb, double v, int precision) {
        if (Double.isNaN(v) || Double.isInfinite(v)) {
            sb.append(v);
            return;
        }

        long whole = (long) v;
        double frac = Math.abs(v - whole);

        sb.append(whole);

        if (precision == 0)
            return;

        sb.append('.');

        for (int i = 0; i < precision; i++) {
            frac *= 10;
            int digit = (int) frac;
            sb.append((char) ('0' + digit));
            frac -= digit;
        }
    }
}
