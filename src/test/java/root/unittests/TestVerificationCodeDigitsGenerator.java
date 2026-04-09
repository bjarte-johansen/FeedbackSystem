package root.unittests;

import org.junit.jupiter.api.Test;
import root.includes.NorwegianTimeAgoTextFormatter;
import root.includes.VerificationCodeDigitsGenerator;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestVerificationCodeDigitsGenerator {
    @Test
    public void test() {
        for(int i=0; i<20; i++) {
            String code = VerificationCodeDigitsGenerator.generate(6);
            assertEquals(6, code.length());
            assertTrue(code.matches("\\d{" + i + "}"), "Generated code should only contain digits");
        }
    }
}
