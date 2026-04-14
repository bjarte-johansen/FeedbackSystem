package root.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/**
 * Annotation to be used on controller methods that should only be accessible by users with the ADMIN role. This
 * annotation is a shorthand for @PreAuthorize("hasRole('ADMIN')") and can be used to improve readability and
 * maintainability of the codebase. By using this annotation, we can easily identify which endpoints are restricted to
 * admin users and ensure that the appropriate security measures are in place.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN')")
public @interface AdminOnly {}