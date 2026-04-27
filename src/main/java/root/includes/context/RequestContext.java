package root.includes.context;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import root.app.AppConfig;
import root.includes.logger.Logger;
import root.models.tenant.Tenant;
import root.services.tenant.TenantResolver;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;


/**
 * Class to handle setting up Tenant and Schema for the current Spring Request. Intercepts and handles all calls to
 * controller routes. Note that tenant must be handled in public scope, as this schema provides storage for tenant
 * settings and data.
 */

@Component("myRequestContextFilter")
@Order(1)
public class RequestContext extends OncePerRequestFilter {
    public static boolean VERBOSE = true;

    // suffixes for files that dont need route handling
    private static HashSet<String> STATIC_RESOURCE_FILE_SUFFIXES = new HashSet<String>(List.of(
        "htm", "html", "jpg", "png", "webp", "gif", "css", "js", "ico", "json"
    ));

    @Autowired
    TenantResolver tenantResolver;


    /**
     * Method that is ran at start of every request that is not a common file (see STATIC_RESOURCE_FILE_SUFFIXES.
     * Finds and set tenant for request
     * Sets schema for request (based on tenant schema name)
     *
     * @param req
     * @param res
     * @param chain
     */

    protected void beforeRequest(
        HttpServletRequest req,
        HttpServletResponse res,
        FilterChain chain
    ) {
        // resolve tenant using a tenant resolve service and set tenant & schema
        Tenant tenant = SchemaContext.scopeSchema("public", () -> tenantResolver.resolve(req));
        checkState(tenant != null, "Failed to resolve tenant");

        // set tenant
        TenantContext.set(tenant);

        // set schema
        SchemaContext.set(tenant.getSchemaName());
    }

    /**
     * Method that is ran after of every request that is not a common file (see STATIC_RESOURCE_FILE_SUFFIXES.
     * Removes tenant from context
     * Removes schema from context
     */

    protected void afterRequest(
        HttpServletRequest req,
        HttpServletResponse res,
        FilterChain chain
    ) {
        // remove tenant
        TenantContext.remove();

        // remove tenant from AppRequestSchema
        SchemaContext.remove();
    }


    private String getFilenameSuffix(String path) {
        int dot = path.lastIndexOf('.');
        return (dot > -1) ? path.substring(dot + 1) : null;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest req,
        HttpServletResponse res,
        FilterChain chain
    ) throws ServletException, IOException {
        // handle common files, no need for context for these
        String suffix = getFilenameSuffix(req.getRequestURI());
        if (STATIC_RESOURCE_FILE_SUFFIXES.contains(suffix)) {
            chain.doFilter(req, res);
            return;
        }

        // handle other routes
        logEnter(req);
        try {
            beforeRequest(req, res, chain);
            chain.doFilter(req, res);
        } finally {
            afterRequest(req, res, chain);
            logLeave(req);
        }
    }


    /*
    methods called before and after request
     */

    private void logEnter(HttpServletRequest req) {
        if (VERBOSE) {
            Logger.log("# BEGIN REQUEST");
            Logger.enter();
            Logger.log("URI: " + req.getRequestURI());
            Logger.log("ServerName: " + req.getServerName());
            Logger.enter();

            // print parameters
            printRequestParameters(req);
        }
    }

    private void logLeave(HttpServletRequest req) {
        if (VERBOSE) {
            Logger.leave();
            Logger.leave();
            Logger.log("# END REQUEST\n");
        }
    }


    /*
    print request parameters for debugging
     */

    private void printRequestParameters(HttpServletRequest req) {
        if (!(AppConfig.CONTROLLER_PRINT_REQUEST_PARAMS)) {
            return;
        }

        Logger.withScope("Request parameters:", () -> {
            if (req.getParameterMap().isEmpty()) {
                Logger.log("No request parameters");
                return;
            }

            for (var entry : req.getParameterMap().entrySet()) {
                Logger.log("Request parameter: " + entry.getKey() + " = " + String.join(", ", entry.getValue()));
            }
        });
    }
}