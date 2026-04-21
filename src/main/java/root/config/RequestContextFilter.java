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
import root.database.DataSourceManager;
import root.includes.logger.Logger;
import root.models.Tenant;
import root.repositories.TenantRepository;
import root.services.TenantResolver;

import java.io.IOException;
import java.sql.Connection;
import java.util.UUID;

//import static com.google.common.base.Preconditions.checkArgument;;
import static com.google.common.base.Preconditions.*;

@Component("myRequestContextFilter")
@Order(1)
public class RequestContextFilter extends OncePerRequestFilter {
    public static boolean VERBOSE = true;

    @Autowired
    private AppContext appContext;

    @Autowired
    TenantResolver tenantResolver;

    @Autowired
    TenantRepository tenantRepo;

    private static ThreadLocal<Tenant> TENANT = new ThreadLocal<>();

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
        if(VERBOSE) {
            Logger.log("Request (#" + UUID.randomUUID().toString() + "), " + req.getRequestURI());
            Logger.log("ServerName: " + req.getServerName());
            Logger.enter();
        }

        // resolve tenant for this request, and set them in the AppRequestSchema for
        // use in the controllers and other downstream code. We also validate the resolved values and throw an exception
        // if they are invalid. Note: in a real application, you would likely want to have more robust tenant resolution
        // and validation logic, and also handle exceptions more gracefully (e.g. by returning an error response
        // instead of throwing an exception).

        Tenant tenant;
        String tenantSchema;

        // we have to resolve tenant in 'public' schema, otherwise we get "relation \"tenant\"
        try(var a = AppRequestSchema.withThreadSchema("public")) {
//            // check that connections are working
//            try {
//                Connection conn = DataSourceManager.getConnection();
//                if(conn == null){
//                    throw new Exception("Aborting before tenant resolve, Connection is null");
//                }
//                conn.close();
//            } catch (Exception e) {
//                throw new RuntimeException("Error occurred trying to find connection", e);
//            }

            // check resolving tenant
            try {
                tenant = tenantResolver.resolve(req);
                if(tenant == null)
                    throw new RuntimeException("Tenant not found, aborting request");
            }catch(Exception e) {
                Logger.log(e.getMessage());
                throw new RuntimeException("Error resolving tenant information", e);
            }

            // update tenant threadlocal
            TENANT.set(tenant);
            if(VERBOSE) Logger.log("Resolved tenant: " + formatTenantInfo(tenant));

        } catch (Exception e) {
            Logger.log(e.getMessage());
            throw new RuntimeException("Error resolving tenant information, aborting request", e);
        }

        // set schema and tenant id
        AppRequestSchema.set(tenant.getSchemaName());

        try {
            // BEFORE request (always runs)
            printRequestParameters(req);

            // runs controller route and other filters (if any)
            chain.doFilter(req, res);

        } finally {
            // AFTER request (always runs)
            tenant = TENANT.get();

            // remove tenant
            TENANT.remove();

            // remove tenant from AppRequestSchema
            AppRequestSchema.remove();

            if(VERBOSE) {
                Logger.leave().log("Finished request for: " + formatTenantInfo(tenant));
                Logger.log("--------------------------------------------------");
                Logger.log("");
            }
        }
    }

    private String formatTenantInfo(Tenant tenant) {
        return "Tenant{id=" + tenant.getId() + ", name=" + tenant.getName() + ", domain=" + tenant.getDomain() + ", tenantSchema=" + tenant.getSchemaName() + "}";
    }

    // print request parameters for debugging
    private void printRequestParameters(HttpServletRequest req) {
        if(VERBOSE && AppConfig.CONTROLLER_PRINT_REQUEST_PARAMS) {
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