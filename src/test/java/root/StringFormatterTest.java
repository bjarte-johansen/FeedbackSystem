package root;

import org.junit.jupiter.api.Test;

import static root.utils.StringFormatter.stringf;

public class StringFormatterTest {
    @Test
    public void logTest() {
        stringf("Hello %s", "world");
        stringf("id=%d", 42);
        stringf("pi=%.2f", 3.14159);
        stringf("%% done");
    }
}
