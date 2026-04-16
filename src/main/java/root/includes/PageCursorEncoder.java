package root.includes;


/**
 * A simple encoder and decoder for the PageCursor. This is used to encode the cursor as a string that can be passed
 * in the URL and decoded back into a PageCursor object. The encoding is simple and not secure, but it's sufficient for
 * our use case. If we wanted to make it more secure, we could encrypt the cursor or use a more complex encoding scheme.
 */

public class PageCursorEncoder {

    /**
     * A simple encoding of the cursor as "offset,limit". This is not secure but it's simple and sufficient for our use
     * case. If we wanted to make it more secure, we could encrypt the cursor or use a more complex encoding scheme.
     *
     * Returns "" if empty to be lenient in accord with parseOrDefault
     *
     * @param c
     * @return
     */

    public static String encode(PageCursor c) {
        if(c == null) return "";
        return c.getOffset() + "," + c.getLimit();
    }


    /**
     * A simple decoding of the cursor from "offset,limit". If the cursor is null or invalid, we return a default cursor
     * with offset 0 and limit Integer.MAX_VALUE. This allows us to handle the case where the client does not provide a
     * cursor or provides an invalid cursor gracefully.
     *
     * @param s string in format "int,int" representing offset and limit
     * @param defaultLimit if set to -1, limit will be Integer.MAX_VALUE
     * @return
     */

    public static PageCursor parseOrDefault(String s, int defaultLimit) {
        int limit = (defaultLimit == -1) ? Integer.MAX_VALUE : defaultLimit;

        if(s == null) return new PageCursor(0, limit);

        try{
            String[] parts = s.split(",");
            return (parts.length == 2)
                ? new PageCursor(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()))
                : new PageCursor(0, limit);
        }catch(NumberFormatException e){
            return new PageCursor(0, limit);
        }
    }

    /** @see #parseOrDefault */
    public static PageCursor parseOrDefault(String s) {
        return parseOrDefault(s, Integer.MAX_VALUE);
    }
}