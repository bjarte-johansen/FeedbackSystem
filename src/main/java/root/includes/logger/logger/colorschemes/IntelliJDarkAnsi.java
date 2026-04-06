package root.includes.logger.logger.colorschemes;

public class IntelliJDarkAnsi implements AnsiColorScheme {
    public static final String RESET = "\033[0m";

    // background
    //public static final String BG = "\033[48;2;43;43;43m"; // #2B2B2B

    // foreground colors (Darcula-like)
    public final String DEFAULT = "\033[38;2;169;183;198m";     // #A9B7C6
    public final String IDENTIFIER = DEFAULT;
    public final String KEYWORD = "\033[38;2;204;120;50m";      // #CC7832
    public final String STRING = "\033[38;2;106;135;89m";      // #6A8759
    public final String NUMBER = "\033[38;2;104;151;187m";     // #6897BB
    public final String SYMBOL = "\033[38;2;200;121;127m";     // #6897BB
    public final String COMMENT = "\033[38;2;128;128;128m";     // #808080
    public final String CLASS = DEFAULT;                      // same as default in many setups

    @Override public String getReset() {return RESET;}
    @Override public String getDefaultColor() {return DEFAULT;}
    @Override public String getString() {return STRING;}
    @Override public String getSymbol() {return SYMBOL;}
    @Override public String getNumber() {return NUMBER;}
    @Override public String getIdentifier() {return IDENTIFIER;}
    @Override public String getKeyword() {return KEYWORD;}
    @Override public String getComment() {return COMMENT;}
    @Override public String getClazz() {return CLASS;}

    public IntelliJDarkAnsi() {}
}
