package root;

import root.database.*;
import root.logger.*;
import root.models.Review;
import root.quicktests.DBTest;
import root.quicktests.FantasyRepoTest;
import root.quicktests.RepoIntegrationTestZone;
import root.repositories.TenantRepository;

import static root.common.utils.Preconditions.checkArgument;
import static root.common.utils.Preconditions.checkNotNull;
//import root.models.repositories.JdbcReviewRepository;
//import static java.lang.StringTemplate.STR;

public class Main {
    static void processNewScanner(String expr){
        SqlQueryMethodNameScanner sp = (new SqlQueryMethodNameScanner()).scan(expr, SqlQueryMethodNameScanner.FLAG_NONE);
        System.out.println("Source: " + sp.sourceString);
        System.out.println("Type: " + sp.methodType);
        System.out.println("Where Clause: " + sp.whereStr);
        System.out.println("Param count: " + sp.paramCount);
        System.out.println("-".repeat(20));
    }

    public static void testScanner(){
        String[] test = new String[] {
            "findByAuthorIdAndExternalId",
            "findByAuthorIdAndScoreLessThanEqualOrAuthorNameEquals",
            "deleteByScoreAndExternalId",
            "findByActiveFalse"
        };

        for (String s : test) {
            processNewScanner(s);
        }
    }

    //
    public static void printWarnings(){
        System.out.println("Project warnings:");
        System.out.println("jsonb support is not implemented yet and will be added in the future. For now, you can use the JdbcReviewRepository directly for testing purposes.");
        System.out.println();
    }

    public static void main(String[] args) throws Throwable{
        boolean bPrintMetaData = true;

        if(bPrintMetaData)
            DB.printMetaData();

        DataSource.warmp(0, DataSource.TEST);


        EntityMeta meta = EntityMeta.create(Review.class);


        Logger.log("--------------------------------------------------------------------");;
        try(var ignore = Logger.scope("Printing EntityMeta for Review...")) {
            Logger.log("EntityMeta for Review:");
            Logger.log("Meta.toString(), " + meta.toString());
            Logger.log("-".repeat(20));
            Logger.log("--------------------------------------------------------------------");;
        }

        // TODO: DO NOT REMOVE
        printWarnings();

        //JdbcRepoTest.testFantasyProx();
        FantasyRepoTest fantasyRepoTest = new FantasyRepoTest();
        fantasyRepoTest.run();

        var tenantRepo = RepositoryProxyConstructor.create(TenantRepository.class);
        var l = tenantRepo.findAll();
        l.forEach(System.out::println);

        if(false) return;
        if(true) return;

        /*
        UnaryOperator<String> fnMapString = s -> "(" + s + ")";
        String[] test = {"a", "b", "c"};
        String[] joined = SqlFactory.mapArray(test, fnMapString);
        System.out.println(String.join(", ", joined));
        */

        if(false) {
            System.out.println(Logger.getConfig());

            DBTest.create()
                .clean();

            try {
                DBTest.create()
                    .run();

                try(var p = Logger.scope("Running proxy test...")){
                    RepoIntegrationTestZone.testProxy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                //DBTest.clean();
            }
        }
    }
}