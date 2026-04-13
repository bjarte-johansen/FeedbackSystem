package root.unittests.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import root.services.PasswordService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TestPasswordService {
    @Autowired
    private PasswordService passwordService;


    @Test
    public void testValidPasswordGenerator() {
        List<String> validPasswords = List.of("password1", "12345678", "qwertyuiop", "letmein123", "admin1234");
        validPasswords.forEach(password -> {
            String hash = passwordService.encode(password);
            assertTrue(passwordService.matches(password, hash));
            assertThrows(IllegalArgumentException.class, () -> passwordService.matches(password, null));
            assertThrows(StringIndexOutOfBoundsException.class, () -> passwordService.matches(password, ""));
        });


    }

    @Test
    public void testInvalidPassword() {
        List<String> invalidPasswords = new ArrayList<>(Arrays.asList(null, "", "short", "   ", "\t\n"));
        invalidPasswords.forEach(password -> {
            assertThrows(IllegalArgumentException.class, () -> passwordService.encode(password));
            assertThrows(IllegalArgumentException.class, () -> passwordService.matches(password, "doesnt matter"));
        });
    }
}
