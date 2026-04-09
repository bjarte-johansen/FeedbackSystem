package root.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import root.app.AppConfig;
import root.app.AppContext;
import root.app.AppRequestSchema;
import root.includes.logger.Logger;

import java.io.IOException;

import static root.common.utils.Preconditions.checkArgument;

@Component("myRequestContextFilter")
@Order(1)
public class RequestContextFilter extends OncePerRequestFilter {
    private static int requestFilterCount = 0;


    private Long resolveTenantId(HttpServletRequest req, boolean required) {
        if(AppConfig.OVERRIDE_TENANT){
            return AppConfig.OVERRIDE_TENANT_ID;
        }


        String tenantParam = req.getParameter("tenantId");
        if (tenantParam == null || tenantParam.isBlank()) {
            if(required) {
                throw new RuntimeException("Missing tenant parameter");
            }

            return -1L;
        }

        try {
            // TODO: return actual tenant schema based on request, e.g. by looking up tenant ID in database or using a
            //  header value

            return Long.parseLong(tenantParam);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Resolve multi tenant id is not implemented", e);
        }
    }

    private String resolveTenantSchema(HttpServletRequest req){
        if(AppConfig.OVERRIDE_TENANT){
            return AppConfig.OVERRIDE_TENANT_SCHEMA;
        }

        if (AppConfig.USE_TEST_TENANT) {
            return "test";
        }

        // TODO: return actual tenant schema based on request, e.g. by looking up tenant ID in database or using a
        //  header value
        throw new RuntimeException("Resolving multi tenant schema is not implemented");
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

        // open a logging block for this request, so all logs within this block will be grouped together with the
        // request URI and filter count for easier debugging
        Logger.log("Request (#" + requestFilterCount + "), " + req.getRequestURI());
        Logger.enter();


        // resolve tenantId and tenantSchema for this request, and set them in the AppContext and AppRequestSchema for
        // use in the controllers and other downstream code. We also validate the resolved values and throw an exception
        // if they are invalid. Note: in a real application, you would likely want to have more robust tenant resolution
        // and validation logic, and also handle exceptions more gracefully (e.g. by returning an error response
        // instead of throwing an exception).

        Long tenantId;
        String tenantSchema;

        try {
            // resolve
            tenantId = resolveTenantId(req, false);
            tenantSchema = resolveTenantSchema(req);

            // validate
            checkArgument(tenantId > 0, "Invalid tenant ID: " + tenantId);
            checkArgument(tenantSchema != null && !tenantSchema.isBlank(), "Unable to find tenant schema, aborting request");

            Logger.log("Resolved tenant information: id = " + tenantId + ", schema = " + tenantSchema);

            // set schema and tenant id
            AppContext appContext = AppContext.getSingleton();
            appContext.setTenantId(tenantId);
            AppRequestSchema.set(tenantSchema);

            Logger.log("Received request for tenant (id: " + tenantId + ", schema: " + tenantSchema + ")");
        }catch (Exception e) {
            Logger.error("Error resolving tenant information: " + e.getMessage(), e);
             throw new RuntimeException(e);
        }

        requestFilterCount++;




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