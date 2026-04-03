package root.app;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import root.database.DataSourceManager;
import root.logger.Logger;

import java.io.IOException;

import static root.common.utils.Preconditions.checkArgument;

@Component("myRequestContextFilter")
@Order(1)
public class RequestContextFilter extends OncePerRequestFilter {
    private static int requestFilterCount = 0;

    private int resolveTenantId(HttpServletRequest req, boolean required) {
        String tenantParam = req.getParameter("tenantId");
        if (tenantParam == null) {
            if(required) {
                throw new RuntimeException("Missing tenant parameter");
            }

            return -1;
        }

        try {
            return Integer.parseInt(tenantParam);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid tenant parameter: " + tenantParam, e);
        }
    }

    private String resolveTenantSchema(HttpServletRequest req){
        //int id = resolveTenantId(req, true);
        return "test";
    }


    @Override
    protected void doFilterInternal(
        HttpServletRequest req,
        HttpServletResponse res,
        FilterChain chain) throws ServletException, IOException {

        String tenantSchema;
        boolean USE_TEST_TENANT = true;

        requestFilterCount++;

        if(USE_TEST_TENANT){
            tenantSchema = "test";
        }else {
            tenantSchema = resolveTenantSchema(req);
        }
        checkArgument(tenantSchema != null && !tenantSchema.isBlank(), "Unable to find tenant schema, aborting request");

        // open a logging block for this request, so all logs within this block will be grouped together with the
        // request URI and filter count for easier debugging
        var logBlock = Logger.scope("Request, " + req.getRequestURI() + " (" + requestFilterCount + ")");
        Logger.log("Received request for tenant schema: " + tenantSchema);

        for(var entry : req.getParameterMap().entrySet()) {
            Logger.log("Request parameter: " + entry.getKey() + " = " + String.join(", ", entry.getValue()));
        }

        try {
            // BEFORE request (always runs)

            // set schema for connections
            AppRequestContext.setTenantSchemaForThread(tenantSchema);

            // runs controller route and other filters (if any)
            // ex @GetMapping("/") in will run after this line if route is "/"
            chain.doFilter(req, res);

        } finally {
            // AFTER request (always runs)
            AppRequestContext.clearTenantSchemaForThread();

            logBlock.close();
        }
    }
}