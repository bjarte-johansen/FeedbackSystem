package root;

import java.util.ArrayList;
import java.util.List;

public class UpperCaseTokenizer{
    public static int TOK_AND = 1;
    public static int TOK_OR = 2;
    public static int TOK_OTHER = 3;

    public static final String AND_STR = "AND";
    public static final String OR_STR = "OR";

    public static record Token(int id, String text) {
        public Token(int id) {
            this(id, null);
        }
        public boolean isAnd(){
            return id == TOK_AND;
        }
        public boolean isOr(){
            return id == TOK_OR;
        }
        public boolean isCombiner(){
            return id == TOK_AND || id == TOK_OR;
        }
        public boolean matchStr(String s){
            return text != null && text.equals(s);
        }
        public boolean matchStrOr(String... s){
            if(text != null) {
                for (String str : s) {
                    if (text.equals(str))
                        return true;
                }
            }
            return false;
        }
        public boolean matchId(int id){
            return this.id == id;
        }
        public boolean matchStrIgnoreCase(String s){
            return text != null && text.equalsIgnoreCase(s);
        }
        public String toString(){
            if(id == TOK_AND) return "(AND)";
            else if(id == TOK_OR) return "(OR)";
            else return "(\"" + text + "\")";
        }
    }

    public static Token createToken(String src, int start, int end, char prevCh){
        if("AND".regionMatches(true, 0, src, start, 3)) {
            return new Token(TOK_AND);
        }else if ("OR".regionMatches(true, 0, src, start, 2)) {
            return new Token(TOK_OR);
        }else {
            return new Token(TOK_OTHER, src.substring(start, end));
        }
    }

    public static List<Token> tokenize(String src) throws Exception{
        // Split the input string into tokens based on uppercase letters
        List<Token> tokens = new ArrayList<>(128);
        int prev = 0;
        int pos = 0;
        int n = src.length();
        char prevCh = 0;

        while( pos < n ){
            char ch = src.charAt(pos);

            if(Character.isUpperCase(ch)) {
                while( pos < n && !Character.isUpperCase(ch = src.charAt(pos))) {
                    if(!Character.isLetter(ch))
                        throw new Exception("Invalid character '" + ch + "' at position " + pos + " in method name: " + src);

                    pos++;
                }

                tokens.add(createToken(src, prev, pos, prevCh));
                prev = pos;
                prevCh = ch;
            }

            pos++;
        }

        if(prev < n) {
            tokens.add(createToken(src, prev, pos, prevCh));
        }

        return tokens;
    }
}
