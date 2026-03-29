package root.database;

import root.AppConfig;

public class Bugs {
    public static String getDefaultEntityIdName(){
        return AppConfig.DEFAULT_ENTITY_ID_NAME;
    }
    public static String getQuoteSymbol() {
        return AppConfig.DEFAULT_IDENTIFIER_QUOTE_STRING;
    }
}
