package root;

import root.database.*;
import root.logger.*;
//import root.models.repositories.JdbcReviewRepository;

import java.util.ArrayList;
import java.util.List;


//import static java.lang.StringTemplate.STR;

public class Main {
    static int TZ_POS;
    static int TZ_LEN;

    public static boolean isValidArrayIndex(int index, int length) {
        return index >= 0 && index < length;
    }

    public static UpperCaseTokenizer.Token skipStr(List<UpperCaseTokenizer.Token> tokens, String s) {
        if ((TZ_POS < TZ_LEN) && tokens.get(TZ_POS).matchStr(s)) {
            TZ_POS++;
            return tokens.get(TZ_POS - 1);
        }
        return null;
    }
    public static UpperCaseTokenizer.Token skipStrOr(List<UpperCaseTokenizer.Token> tokens, String s) {
        if ((TZ_POS < TZ_LEN) && tokens.get(TZ_POS).matchStrOr(s)) {
            TZ_POS++;
            return tokens.get(TZ_POS - 1);
        }
        return null;
    }

    public static void interpretTokens(List<UpperCaseTokenizer.Token> tokens) {
        int pos = 0;
        int n = tokens.size();
        List<List<UpperCaseTokenizer.Token>> tokenGroups = new ArrayList<>();
        List<UpperCaseTokenizer.Token> group = new ArrayList<>();

        TZ_POS = 0;
        TZ_LEN = tokens.size();


        tokenGroups.add(group);
        while(pos < n){
            if(tokens.get(pos).matchStrOr("LessThan", "LessThanEqual", "Greater", "GreaterThanEqual", "Equal", "NotEqual")){
                if(!group.isEmpty())
                    tokenGroups.add(group = new ArrayList<>());
                group.add(tokens.get(pos));

                tokenGroups.add(group = new ArrayList<>());
                pos++;
                continue;
            }

            if(tokens.get(pos).id() == UpperCaseTokenizer.TOK_AND || tokens.get(pos).id() == UpperCaseTokenizer.TOK_OR) {
                if(!group.isEmpty())
                    tokenGroups.add(group = new ArrayList<>());
                group.add(tokens.get(pos));

                tokenGroups.add(group = new ArrayList<>());
                pos++;
                continue;
            }

            group.add(tokens.get(pos));
            pos++;
        }

        if(group.isEmpty())
            tokenGroups.removeLast();

        try (var ignore1 = Logger.scope("Tokenizer groups:")) {
            for (var g : tokenGroups) {
                try (var ignore2 = Logger.scope("group:")) {
                    for (var t : g) {
                        Logger.log(t);
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Throwable{
        //testScanString();

        //System.setOut(new CallerPrintStream(System.out));

        String[] test = new String[]{
            "findByAuthorIdAndExternalId",
            "findByAuthorIdAndScoreLessOrEqualOrAuthorName",
            "findByScoreAndExternalId"
        };

        var tokens = UpperCaseTokenizer.tokenize("findByAuthorIdLessOrEqualAndExternalId");
        interpretTokens(tokens);
        Logger.log("tokens: " + tokens);

        boolean c = false;
        if(!c) {
            return;
        }


        //var tokens = RepoProxyMethodScanner.tokenize(test[1]);


        boolean b = true;
        if(b) {
            System.out.println(Logger.getConfig());

            DBTest.clean();

            //execTest();


            DB.printMetaData();

            try {
                DBTest.run();

                try(var p = Logger.scope("Running proxy test...")){
                    JdbcRepoTest.testProxy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                DBTest.clean();
            }
        }
    }
}