package root.database;

import root.app.AppConfig;

/**
 * this file is only used for root.database to get some entities that do not yet have a nice home to live.
 *
 * No unit testing required
 */

public class Bugs {
    public static String getDefaultEntityIdName(){
        return AppConfig.DEFAULT_ENTITY_ID_NAME;
    }
    public static String getQuoteSymbol() {
        return AppConfig.DEFAULT_IDENTIFIER_QUOTE_STRING;
    }
}
