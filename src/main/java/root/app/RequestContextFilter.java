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

    @Override
    protected void doFilterInternal(
        HttpServletRequest req,
        HttpServletResponse res,
        FilterChain chain) throws ServletException, IOException {

        int tenantId = -1;
        requestFilterCount++;

        try {
            // BEFORE request

            System.out.println("beforeRequest (" + requestFilterCount + ")");
            System.out.println("Request URI: " + req.getRequestURI());

            tenantId = resolveTenantId(req, false);
            if(tenantId > -1) {
                DataSource.THREAD_LOCAL_SCHEMA.set("test");
            }

            System.out.println(req);

            chain.doFilter(req, res);

        } finally {
            // AFTER request (always runs)

            System.out.println("afterRequest (" + requestFilterCount + ")");

            if(tenantId > -1) {
                DataSource.THREAD_LOCAL_SCHEMA.remove();
            }
        }
    }
}