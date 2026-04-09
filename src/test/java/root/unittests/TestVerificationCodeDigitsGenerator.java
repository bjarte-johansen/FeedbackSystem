package root.unittests;

import org.junit.jupiter.api.Test;
import root.includes.NorwegianTimeAgoTextFormatter;
import root.includes.VerificationCodeDigitsGenerator;
import root.includes.logger.Logger;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestVerificationCodeDigitsGenerator {
    @Test
    public void test() {
        for(int i=1; i<20; i++) {
            String code = VerificationCodeDigitsGenerator.generate(i);
            assertTrue(code != null);
            assertTrue(code.matches("\\d{" + i + "}"), "Generated code should only contain digits");
        }
    }
}
