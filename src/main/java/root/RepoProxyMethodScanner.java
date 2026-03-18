package root;

import root.logger.Logger;

import java.util.ArrayList;
import java.util.List;

class RepoProxyMethodScanner {
    public static record Token(String id, String text) {
        public String toString() {
            if(id != null && !id.isEmpty()) {
                return "(" + id + ")";
            }else{
                return "(\"" + text + "\")";
            }
        }
    }

    public static List<Token> tokenize(String SRC) {
        List<Token> tokens = new ArrayList<>();

        int POS = 0, N = SRC.length();
        while (POS < N) {
            System.out.println(POS + ", "  + SRC.charAt(POS) + ", len: " + N);

            int nextUpper = findNextUpper(SRC, POS + 1);
            if (nextUpper == -1) nextUpper = N;

            String tokenText = SRC.substring(POS, nextUpper);

            if(tokenText.equals("And")){
                tokens.add(new Token("AND", ""));
            } else if ( tokenText.equals("Or")) {
                tokens.add(new Token("OR", ""));
            }else{
                tokens.add(new Token("", tokenText));
            }

            POS = nextUpper;
        }

        Logger.log("Tokens: " + tokens);

        return tokens;
    }

    private static int findNextUpper(String methodName, int pos) {
        for (int i = pos; i < methodName.length(); i++) {
            if (Character.isUpperCase( methodName.charAt(i) )) {
                return i;
            }
        }
        return -1;
    }
}
