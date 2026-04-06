package root.app.includes;

import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * A simple encoder and decoder for the PageCursor. This is used to encode the cursor as a string that can be passed
 * in the URL and decoded back into a PageCursor object. The encoding is simple and not secure, but it's sufficient for
 * our use case. If we wanted to make it more secure, we could encrypt the cursor or use a more complex encoding scheme.
 */

public class PageCursorEncoder {
    //static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * A simple encoding of the cursor as "offset,limit". This is not secure but it's simple and sufficient for our use
     * case. If we wanted to make it more secure, we could encrypt the cursor or use a more complex encoding scheme.
     *
     * @param c
     * @return
     */
    public static String encodeCursor(PageCursor c) {
        return c.getOffset() + "," + c.getLimit();
    }


    /**
     * A simple decoding of the cursor from "offset,limit". If the cursor is null or invalid, we return a default cursor
     * with offset 0 and limit Integer.MAX_VALUE. This allows us to handle the case where the client does not provide a
     * cursor or provides an invalid cursor gracefully.
     *
     * @param cursor
     * @return
     */

    public static PageCursor decodeCursor(String cursor) {
        String[] parts = (cursor == null) ? new String[0] : cursor.split(",");
        if (parts.length == 2) {
            return new PageCursor(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        } else {
            return new PageCursor(0, Integer.MAX_VALUE);
        }
    }
}