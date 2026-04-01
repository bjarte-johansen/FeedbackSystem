package root.app;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import root.database.DataSource;

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

        requestFilterCount++;

        String tenant_schema = resolveTenantSchema(req);
        checkArgument(tenant_schema != null && !tenant_schema.isBlank(), "Unable to find tenant schema, aborting request");

        try {
            // BEFORE request (always runs)

            System.out.println("beforeRequest (" + requestFilterCount + ")");
            System.out.println("Request URI: " + req.getRequestURI());
            System.out.println("Tenant schema set to " + tenant_schema);
            //System.out.println(req);

            // set schema for connections
            DataSource.THREAD_LOCAL_SCHEMA.set(tenant_schema);

            // runs controller route and other filters (if any)
            // ex @GetMapping("/") in will run after this line if route is "/"
            chain.doFilter(req, res);

        } finally {
            // AFTER request (always runs)

            System.out.println("afterRequest (" + requestFilterCount + ")\n\n");

            DataSource.THREAD_LOCAL_SCHEMA.remove();
        }
    }
}