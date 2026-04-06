package root.app.includes;

import java.util.Base64;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PageCursorEncoder {
    static final ObjectMapper MAPPER = new ObjectMapper();

    public static String encodeCursor(PageCursor c) {
        return c.getOffset() + "," + c.getLimit();
    }

    public static PageCursor decodeCursor(String cursor) {
        String[] parts = (cursor == null) ? new String[0] : cursor.split(",");
        if (parts.length == 2) {
            return new PageCursor(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }else{
            return new PageCursor(0, Integer.MAX_VALUE);
        }
    }


    /*
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
     */
}
