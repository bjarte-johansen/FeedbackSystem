package root.auth;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Implemented with chatgpt to "get it right" and modified by me to fit the project. This class provides static methods
 * to check if a user is logged in, if they are an administrator, and to get their username. It uses Spring Security's
 * SecurityContextHolder to access the current authentication information.
 *
 */

@Deprecated
public final class Auth {

    private Auth() {
    }

    public static boolean isLoggedIn() {
        var a = SecurityContextHolder.getContext().getAuthentication();
        return a != null && a.isAuthenticated()
            && !(a instanceof AnonymousAuthenticationToken);
    }

    public static boolean isAdministrator() {
        var a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !isLoggedIn()) return false;

        return a.getAuthorities().stream()
            .anyMatch(x -> x.getAuthority().equals("ROLE_ADMIN") || x.getAuthority().equals("ADMIN"));
    }

    public static String username() {
        var a = SecurityContextHolder.getContext().getAuthentication();
        return (a != null ? a.getName() : null);
    }
}