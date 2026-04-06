package root.app;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;
import root.includes.logger.logger.Logger;

import java.io.IOException;
import java.util.Map;

import static root.common.utils.Preconditions.checkArgument;

@Component("myRequestContextFilter")
@Order(1)
public class RequestContextFilter extends OncePerRequestFilter {
    private static int requestFilterCount = 0;


    private Long resolveTenantId(HttpServletRequest req, boolean required) {
        if (AppConfig.USE_TEST_TENANT) {
            return 1L;
        }

        String tenantParam = req.getParameter("tenantId");
        if (tenantParam == null) {
            if(required) {
                throw new RuntimeException("Missing tenant parameter");
            }

            return -1L;
        }

        try {
            return Long.parseLong(tenantParam);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid tenant parameter: " + tenantParam, e);
        }
    }

    private String resolveTenantSchema(HttpServletRequest req){
        if (AppConfig.USE_TEST_TENANT) {
            return "test";
        }

        // TODO: return actual tenant schema based on request, e.g. by looking up tenant ID in database or using a
        //  header value
        return "test";
    }


    @Override
    protected void doFilterInternal(
        HttpServletRequest req,
        HttpServletResponse res,
        FilterChain chain
    ) throws ServletException, IOException {

        String uri = req.getRequestURI();
        if (uri.endsWith(".js") || uri.endsWith(".css") || uri.endsWith(".json")) {
            chain.doFilter(req, res);
            return;
        }

        Long tenantId;
        String tenantSchema;

        try {
            tenantId = resolveTenantId(req, false);
            tenantSchema = resolveTenantSchema(req);

            checkArgument(tenantId > 0, "Invalid tenant ID: " + tenantId);
            checkArgument(tenantSchema != null && !tenantSchema.isBlank(), "Unable to find tenant schema, aborting request");
        }catch (Exception e) {
            Logger.error("Error resolving tenant information: " + e.getMessage(), e);
             throw new RuntimeException(e);
        }

        requestFilterCount++;
/*
        Map<String, String> vars =
            (Map<String, String>) req.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE
            );
        for(var entry : vars.entrySet()) {
            Logger.log("URI variable: " + entry.getKey() + " = " + entry.getValue());
        }

 */

        // set schema and tenant id
        AppContext.currentTenantId.set(tenantId);
        AppRequestSchema.set(tenantSchema);


        // open a logging block for this request, so all logs within this block will be grouped together with the
        // request URI and filter count for easier debugging
        Logger.log("Request (#" + requestFilterCount + "), " + req.getRequestURI());
        Logger.enter();
        Logger.log("Received request for tenant (id: " + tenantId + ", schema: " + tenantSchema + ")");

        if(AppConfig.CONTROLLER_PRINT_REQUEST_PARAMS) {
            // print request parameters for debugging
            if(req.getParameterMap().isEmpty()) {
                Logger.log("No request parameters");
            } else {
                Logger.log("Request parameters:");
                Logger.enter();
                for (var entry : req.getParameterMap().entrySet()) {
                    Logger.log("Request parameter: " + entry.getKey() + " = " + String.join(", ", entry.getValue()));
                }
                Logger.leave();
                Logger.log("");
            }
        }


        try {
            // BEFORE request (always runs)

            // runs controller route and other filters (if any)
            // ex @GetMapping("/") in will run after this line if route is "/"
            chain.doFilter(req, res);

        } finally {
            // AFTER request (always runs)
            AppRequestSchema.remove();

            Logger.leave();
            Logger.log("Finished request for tenant (id: " + tenantId + ", schema: " + tenantSchema + ")");
            Logger.log("--------------------------------------------------");
            Logger.log("");
        }
    }
}