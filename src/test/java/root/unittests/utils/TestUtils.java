package root.unittests.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import root.includes.Utils;

public class TestUtils {
    @Test
    public void testPassword() {
        List<String> invalidPasswords = Arrays.asList(
            null,              // null
            "",                // empty string
            "        ",        // whitespace only
            "seven7!",         // less than 8 characters
            "_\\/\\/§~£",
            "pass word"        // no uppercase, no digit, no special char
        );

        /*
        TODO: make these not pass tests by changing REGEX used by requireValidPassword
            "PASSWORD",        // no lowercase, no digit, no special char
            "Passw0rd",        // no special char
            "Pass!word",       // no digit
            "12345678",        // no letters, no special char
            "P@ssw0rd ",       // trailing space
            " P@ssw0rd",       // leading space
            "P@ss w0rd",       // space in the middle
            "P@ssw0rd\n",      // newline character
            "P@ssw0rd\t"       // tab character
        );
         */

        for (String invalidPassword : invalidPasswords) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                Utils.requireValidPassword(invalidPassword);
            }, "Expected requireValidPassword to throw for invalid password: " + invalidPassword);

            assertTrue(exception.getMessage().toLowerCase().contains("password"), "Exception message should mention 'password'");
        }

        List<String> validPasswords = List.of(
            "Password123!",     // valid
            "P@ssw0rd",        // valid
            "MySecure#2024",   // valid
            "Admin!2024",      // valid
            "User123$",        // valid
            "Passw0rd!",       // valid
            "S3cure*Pass",     // valid
            "Valid_Pass1",     // valid
            "Complex#2024",    // valid
            "Str0ng!Pass"      // valid
        );

        for (String password : validPasswords) {
            assertDoesNotThrow(
                () -> Utils.requireValidPassword(password),
                "Expected requireValidPassword to not throw for valid password: " + password);
        }
    }

    @Test
    public void test_Utils__hasText() {
        assertFalse(Utils.hasText(null), "Expected hasText(null) to return false");
        assertFalse(Utils.hasText(""), "Expected hasText(\"\") to return false");
        assertFalse(Utils.hasText("   "), "Expected hasText(\"   \") to return false");
        assertTrue(Utils.hasText("text"), "Expected hasText(\"text\") to return true");
        assertTrue(Utils.hasText(" text "), "Expected hasText(\" text \") to return true");
    }

    @Test
    public void test_Utils__parseCsvIntList() {
        assertEquals(List.of(), Utils.parseCsvIntList(null), "Expected parseCsvIntList(null) to return empty list");
        assertEquals(List.of(), Utils.parseCsvIntList(""), "Expected parseCsvIntList(\"\") to return empty list");
        assertEquals(List.of(1, 2, 3), Utils.parseCsvIntList("1, 2, 3"), "Expected parseCsvIntList(\"1, 2, 3\") to return [1, 2, 3]");
        assertEquals(List.of(4, 5, 6), Utils.parseCsvIntList("4;5;6", ";"), "Expected parseCsvIntList(\"4;5;6\", \";\") to return [4, 5, 6]");
        assertThrows(NumberFormatException.class, () -> Utils.parseCsvIntList("1, two, 3"), "Expected parseCsvIntList(\"1, two, 3\") to throw NumberFormatException");
    }

    @Test
    public void test_Utils__requireValidEmail() {
        List<String> invalidEmails = Arrays.asList(
            null,              // null
            "",                // empty string
            "   ",             // whitespace only
            "plainaddress",    // missing @ and domain
            "@no-local-part.com", // missing local part
            "Outlook Contact <",
            "no-at.domain.com", // missing @
            "no-tld@domain",    // missing top-level domain
            "invalid@domain,com", // comma instead of dot
            "invalid@domain..com" // double dot in domain
        );
        invalidEmails.forEach(invalidEmail -> {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Utils.requireValidEmail(invalidEmail),
                "Expected requireValidEmail to throw for invalid email: " + invalidEmail);

            assertTrue(exception.getMessage().toLowerCase().contains("email"), "Exception message should mention 'email'");
        });

        List<String> validEmails = List.of(
            "test@test.com",
            "one@google.com",
            "well@this.is.just.useless.com",
            "hotgirl89@this.is.just.us3less.com"
        );
        validEmails.forEach(validEmail -> {
            assertDoesNotThrow(() -> Utils.requireValidEmail(validEmail),
                "Expected requireValidEmail to not throw for valid email: " + validEmail);
        });
    }

    /**
     * Test in part written by ChatGPT, but also includes some additional cases I thought of. The method is pretty
     * straightforward so I think this is sufficient coverage for it.
     * <p>
     * Uses jackson's ObjectMapper for JSON serialization, so we can rely on it to handle most of the cases correctly.
     * We just need to make sure our ways of using it are correct and rely on integration tests to catch any issues with
     * the ObjectMapper configuration or usage in more complex scenarios. For this unit test, we can focus on basic
     * types and simple objects to verify that toJson is working as expected.
     */

    @Test
    public void test_Utils__toJson() {
        int i = 42;
        assertEquals("42", Utils.toJson(i), "Expected toJson(42) to return \"42\"");
        String s = "abc";
        assertEquals("\"abc\"", Utils.toJson(s), "Expected toJson(\"abc\") to return \"\\\"abc\\\"\"");
        List<Integer> list = List.of(1, 2, 3);
        assertEquals("[1,2,3]", Utils.toJson(list), "Expected toJson(List.of(1, 2, 3)) to return \"[1,2,3]\"");

        class TestObject {
            public int id = 1;
            public String name = "test";
        }
        TestObject obj = new TestObject();
        String expectedJson = "{\"id\":1,\"name\":\"test\"}";
        assertEquals(expectedJson, Utils.toJson(obj), "Expected toJson(TestObject) to return " + expectedJson);
    }

    /**
     * Test in part written by ChatGPT, but also includes some additional cases I thought of. The method is pretty
     * straightforward so I think this is sufficient coverage for it.
     */

    @Test
    public void test_Utils__escapeHtml() {
        assertNull(Utils.escapeHtml(null), "Expected escapeHtml(null) to return null");
        assertEquals("", Utils.escapeHtml(""), "Expected escapeHtml(\"\") to return \"\"");
        assertEquals("Hello, World!", Utils.escapeHtml("Hello, World!"), "Expected escapeHtml(\"Hello, World!\") to return \"Hello, World!\"");
        assertEquals("5 &lt; 10 &amp; 10 &gt; 5", Utils.escapeHtml("5 < 10 & 10 > 5"), "Expected escapeHtml(\"5 < 10 & 10 > 5\") to return \"5 &lt; 10 &amp; 10 &gt; 5\"");
        assertEquals("She said, &quot;Hello!&quot;", Utils.escapeHtml("She said, \"Hello!\""), "Expected escapeHtml(\"She said, \\\"Hello!\\\"\") to return \"She said, &quot;Hello!&quot;\"");
    }
}