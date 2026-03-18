package root.logger.colorschemes;

public class DefaultColorScheme implements AnsiColorScheme {
    private static final String RESET = "\033[0m";

    private static final String COLOR_GREEN = "\033[32m";  // strings
    private static final String COLOR_YELLOW = "\033[33m";  // symbols
    private static final String COLOR_CYAN = "\033[36m";  // identifiers
    private static final String COLOR_MAGENTA = "\033[35m";  // numbers

    public final String DEFAULT = RESET;
    public final String COMMENT = RESET;
    public final String CLASS = RESET;
    public final String STRING = COLOR_GREEN;
    public final String SYMBOL = COLOR_YELLOW;
    public final String NUMBER = COLOR_MAGENTA;
    public final String IDENTIFIER = COLOR_CYAN;
    public final String KEYWORD = COLOR_CYAN;

    @Override public String getReset() {return RESET;}
    @Override public String getDefaultColor() {return DEFAULT;}
    @Override public String getString() {return STRING;}
    @Override public String getSymbol() {return SYMBOL;}
    @Override public String getNumber() {return NUMBER;}
    @Override public String getIdentifier() {return IDENTIFIER;}
    @Override public String getKeyword() {return KEYWORD;}
    @Override public String getComment() {return COMMENT;}
    @Override public String getClazz() {return CLASS;}

    public DefaultColorScheme() {}
}
