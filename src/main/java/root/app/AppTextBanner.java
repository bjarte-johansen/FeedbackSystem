package root.app;

import root.includes.proxyrepo.RepositoryProxyConstructor;
import root.includes.logger.Logger;


/**
 * This class is responsible for printing the banner and warning messages when the application starts.
 * It uses a multi-line string to define the banner and prints it to the console using the
 * Logger class. It also includes methods for printing dashed lines and empty lines for formatting purposes.
 * The banner includes the project name, version, and developer information, as well as a warning message about the project's current state.
 * The print() method is called at the start of the application to display the banner and warnings to the user.
 *
 * TODO: update the banner with the correct project name and developer information, and add any additional warnings or messages as needed.
 */

public class AppTextBanner {
    static String headerText = """
        ██████╗ ███████╗██╗   ██╗██╗███████╗██╗    ██╗
        ██╔══██╗██╔════╝██║   ██║██║██╔════╝██║    ██║
        ██████╔╝█████╗  ██║   ██║██║█████╗  ██║ █╗ ██║
        ██╔══██╗██╔══╝  ╚██╗ ██╔╝██║██╔══╝  ██║███╗██║
        ██║  ██║███████╗ ╚████╔╝ ██║███████╗╚███╔███╔╝
        ╚═╝  ╚═╝╚══════╝  ╚═══╝  ╚═╝╚══════╝ ╚══╝╚══╝
        
        Version: 1.0.0, DAT109 Project, 2026
        Developed by: Bjarte Johansen, Fahad Ahmed, Marcus Lowenstein, Øyvind Nordeide, 
        Prince Nixon Alaoysius, Ahmad Ahmed.
        System: ReView Feedback Engine
        """;


    /**
     * Prints the application banner and warning messages to the console.
     */
    public static void print(){

        // TODO: rette navn i reviewbanneren, jeg skrev de etter hukommelse.

        printDashedLines(2);
        printEmptyLines(2);

        Logger.log(headerText);

        printEmptyLines(2);
        printDashedLines(3);
        printEmptyLines(1);

        String wrapped = RepositoryProxyConstructor.getDeveloperWarningMessages()
            .replaceAll("(.{1," + 80 + "})(?:\\s+|$)", "$1\n");
        Logger.log(wrapped);

        printEmptyLines(1);
        printDashedLines(3);
        printEmptyLines(2);
    }

    public static void printDashedLines(int n){
        for(int i = 0; i < n; i++)
            Logger.log("-".repeat(80));
    }

    public static void printEmptyLines(int n){
        for(int i = 0; i < n; i++)
            Logger.log("");
    }
}
