package root;

import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EndpointTestUtils {
    public static void assertIs4xxOr5xx(MvcResult res) throws Exception {
        int s = res.getResponse().getStatus();
        assertTrue(s >= 400 && s < 600);
    }
}
