package root.beans;

import org.springframework.stereotype.Component;

import java.util.Locale;

public class FormatUtils {
    public String format2(Double v) {
        return String.format(Locale.US, "%.2f", v);
    }
}
