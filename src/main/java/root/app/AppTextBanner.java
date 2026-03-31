package root.app;

import root.RepositoryProxyConstructor;
import root.common.utils.KissWordWrapper;
import root.logger.Logger;

public class AppTextBanner {
    public static void print(){
        String tmp = """
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
        // TODO: rette navn i reviewbanneren, jeg skrev de etter hukommelse.

        printDashedLines(2);
        printEmptyLines(2);

        Logger.log(tmp);

        printEmptyLines(2);
        printDashedLines(3);
        printEmptyLines(1);

        Logger.log(KissWordWrapper.wordwrap(RepositoryProxyConstructor.getDeveloperWarningMessages(), 80));

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
