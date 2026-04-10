package root.unittests.utils;

import org.junit.jupiter.api.Test;
import root.includes.VerificationCodeDigitsGenerator;

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
