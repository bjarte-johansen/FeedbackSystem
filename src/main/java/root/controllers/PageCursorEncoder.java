package root.controllers;

import java.awt.*;
import java.util.Base64;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PageCursorEncoder {
    static final ObjectMapper MAPPER = new ObjectMapper();

    public static String encodeCursor(PageCursor c) {
        try {
            byte[] json = MAPPER.writeValueAsBytes(c);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PageCursor decodeCursor(String cursor) {
        try {
            byte[] json = Base64.getUrlDecoder().decode(cursor);
            return MAPPER.readValue(json, PageCursor.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
