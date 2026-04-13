package root.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import root.app.AppConfig;
import root.app.AppContext;
import root.app.AppRequestSchema;
import root.includes.logger.Logger;
import root.models.Tenant;
import root.repositories.TenantRepository;
import root.services.TenantResolver;

import java.io.IOException;
import java.util.UUID;

import static root.common.utils.Preconditions.checkArgument;

@Component("myRequestContextFilter")
@Order(1)
public class RequestContextFilter extends OncePerRequestFilter {
    @Autowired
    private AppContext appContext;

    @Autowired
    TenantResolver tenantResolver;

    @Autowired
    TenantRepository tenantRepo;

    protected void beforeRequest(
        HttpServletRequest req,
        HttpServletResponse res,
        FilterChain chain) {
        // this method can be used to execute any logic before the request is processed by the controller, e.g. logging, authentication, etc.
    }

    protected void afterRequest(
        HttpServletRequest req,
        HttpServletResponse res,
        FilterChain chain){

    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest req,
        HttpServletResponse res,
        FilterChain chain
    ) throws ServletException, IOException {

        // handle common files
        String uri = req.getRequestURI();
        if (uri.endsWith(".js") || uri.endsWith(".css") || uri.endsWith(".json") || uri.endsWith(".ico") || uri.endsWith(".png") || uri.endsWith(".jpg") || uri.endsWith(".jpeg") || uri.endsWith(".gif")) {
            chain.doFilter(req, res);
            return;
        }


        // open a logging block for this request, so all logs within this block will be grouped together with the
        // request URI and filter count for easier debugging
        Logger.log("Request (#" + UUID.randomUUID().toString() + "), " + req.getRequestURI());
        Logger.log("ServerName: " + req.getServerName());
        Logger.enter();

        // resolve tenant for this request, and set them in the AppRequestSchema for
        // use in the controllers and other downstream code. We also validate the resolved values and throw an exception
        // if they are invalid. Note: in a real application, you would likely want to have more robust tenant resolution
        // and validation logic, and also handle exceptions more gracefully (e.g. by returning an error response
        // instead of throwing an exception).

        Tenant tenant;
        String tenantSchema;

        // resolve tenant and set schema (resolving is done in schema 'public')
        try {
            // some bug means we have to reolve tenant in 'public' schema, otherwise we get "relation \"tenant\"
            // does not exist" error when trying to resolve tenant in the first place, which is really weird.
            // TODO: fix in URI for connection
            try(var a = AppRequestSchema.withThreadSchema("public")) {
                tenant = tenantResolver.resolve(req);
            } catch (Exception e) {
                Logger.log(e.getMessage());
                throw new RuntimeException("Error resolving tenant information, aborting request", e);
            }

            if(tenant == null) throw new RuntimeException("Fatal error, tenant not found, aborting request");

            // validate schema name (TODO: make it better)
            tenantSchema = tenant.getSchemaName();
            checkArgument(tenantSchema != null && !tenantSchema.isBlank(), "Invalid tenant schema name, aborting request");

            // set schema and tenant id
            AppRequestSchema.set(tenantSchema);

            // log
            Logger.log("Resolved tenant: " + formatTenantInfo(tenant));
        }catch (Exception e) {
            Logger.error("Error resolving tenant information: " + e.getMessage(), e);
             throw new RuntimeException(e);
        }

        printRequestParameters(req);

        try {
            // BEFORE request (always runs)

            // runs controller route and other filters (if any)
            // ex @GetMapping("/") in will run after this line if route is "/"
            chain.doFilter(req, res);

        } finally {
            // AFTER request (always runs)
            AppRequestSchema.remove();

            Logger.leave();
            Logger.log("Finished request for: " + formatTenantInfo(tenant));
            Logger.log("--------------------------------------------------");
            Logger.log("");
        }
    }

    private String formatTenantInfo(Tenant tenant) {
        return "Tenant{id=" + tenant.getId() + ", name=" + tenant.getName() + ", domain=" + tenant.getDomain() + ", tenantSchema=" + tenant.getSchemaName() + "}";
    }

    // print request parameters for debugging
    private void printRequestParameters(HttpServletRequest req) {
        if(AppConfig.CONTROLLER_PRINT_REQUEST_PARAMS) {
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
     }
}