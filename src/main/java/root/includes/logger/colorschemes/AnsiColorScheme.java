package root.includes.logger.colorschemes;

import root.includes.logger.ElementType;

public interface AnsiColorScheme {
    String getReset();
    String getDefaultColor();

    String getString();
    String getSymbol();
    String getNumber();
    String getIdentifier();
    String getKeyword();
    String getComment();
    String getClazz();

    default String getColor(ElementType type)  {
        return switch (type) {
            case STRING -> getString();
            case SYMBOL -> getSymbol();
            case NUMBER -> getNumber();
            case IDENTIFIER -> getIdentifier();
            case KEYWORD -> getKeyword();
            case COMMENT -> getComment();
            case CLASS -> getClazz();
            default -> getDefaultColor();
        };
    }
}
