package root.controllers;

import jakarta.servlet.http.HttpServletRequest;

public class ControllerUtils {
    public static void dumpRequestParams(HttpServletRequest req) {
        System.out.println("BEGIN request-params:");
        req.getParameterMap().forEach((k, v) ->
            System.out.println("\t" + k + " = " + java.util.Arrays.toString(v))
        );
        System.out.println("END request-params");
    }
}
