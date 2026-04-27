package root.unittests.services;

import org.junit.jupiter.api.Test;
import root.services.host.HostNameNormalizer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestHostNormalizer {
    private String norm(String s){
        return HostNameNormalizer.normalize(s);
    }

    @Test
    public void testNormalize() {
        assertEquals("power.no", norm("power.no"));
        assertEquals("www.power.no", norm("www.power.no"));
        assertEquals("www.power.no", norm("WWW.POWER.NO"));
        assertEquals("power.no", norm("http://power.no"));
        assertEquals("power.no", norm("https://power.no"));
        assertEquals("www.power.no", norm("http://www.power.no"));
        assertEquals("www.power.no", norm("https://www.power.no"));

        assertEquals("sub.google.com", norm("sub.google.com"));
        assertEquals("www.sub.google.com", norm("http://www.sub.google.com"));
        assertEquals("www.sub.google.com", norm("https://www.sub.google.com"));
    }
}
