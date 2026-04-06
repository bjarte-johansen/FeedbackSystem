package root.includes.logger.logger.colorschemes;

public class HighContrastScheme implements AnsiColorScheme {
    private static final String RESET = "\033[0m";

    private static final String BRIGHT_GREEN = "\033[92m";
    private static final String BRIGHT_YELLOW = "\033[93m";
    private static final String BRIGHT_CYAN = "\033[96m";
    private static final String BRIGHT_MAGENTA = "\033[95m";
    private static final String BRIGHT_WHITE = "\033[97m";

    public final String DEFAULT = RESET;
    public final String KEYWORD = BRIGHT_CYAN;
    public final String COMMENT = RESET;
    public final String STRING = BRIGHT_GREEN;     // readable, calm
    public final String SYMBOL = BRIGHT_YELLOW;    // strong contrast
    public final String NUMBER = BRIGHT_MAGENTA;   // pops but not harsh
    public final String IDENTIFIER = BRIGHT_WHITE;      // crisp
    public final String CLASS = BRIGHT_CYAN;       // distinct and readable

    @Override public String getReset() {return RESET;}
    @Override public String getDefaultColor() {return DEFAULT;}
    @Override public String getString() {return STRING;}
    @Override public String getSymbol() {return SYMBOL;}
    @Override public String getNumber() {return NUMBER;}
    @Override public String getIdentifier() {return IDENTIFIER;}
    @Override public String getKeyword() {return KEYWORD;}
    @Override public String getComment() {return COMMENT;}
    @Override public String getClazz() {return CLASS;}

    public HighContrastScheme() {}
}
