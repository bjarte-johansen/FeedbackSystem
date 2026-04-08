package root.includes.logger;

@Deprecated
public class CustomStringBuilder {
    private final StringBuilder out;
    public final String resetColor;

    public CustomStringBuilder(int capacity, String resetColor) {
        out = new StringBuilder(capacity);
        this.resetColor = resetColor;
    }

    public CustomStringBuilder append(Object o) {
        out.append(o);
        return this;
    }

    public CustomStringBuilder append(char c) {
        out.append(c);
        return this;
    }

    public CustomStringBuilder append(Object o, String color) {
        String s = o == null ? "null" : String.valueOf(o);
        return append(s, 0, s.length(), color);
    }

    public CustomStringBuilder append(char c, String color) {
        String s = String.valueOf(c);
        return append(s, 0, s.length(), color);
    }

    public CustomStringBuilder append(String s, int start, int end, String color) {
        out.append(color);
        out.append(s, start, end);
        out.append(resetColor);
        return this;
    }

    public String toString() {
        return out.toString();
    }
}
