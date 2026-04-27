package root.auth;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import root.app.AppConfig;
import root.includes.context.SchemaContext;
import root.includes.logger.Logger;
import root.repositories.tenant.TenantRepository;

import java.net.URLEncoder;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    /**
     * Defines the security filter chain, which configures how HTTP requests are secured. It disables CSRF protection
     * for simplicity, allows unauthenticated access to static resources and the admin login page, and requires
     * authentication for any other admin pages. It also configures form-based login with custom success and failure
     * handlers that print login results to the console and redirect accordingly.
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers("/admin/login").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")

                .successHandler((req, res, auth) -> {
                    // for logging purposes
                    Logger.log("LOGIN OK: " + auth.getName());

                    res.sendRedirect("/admin/dashboard");
                })
                .failureHandler((req, res, ex) -> {
                    Logger.log("LOGIN FAIL: " + ex.getMessage());

                    String errText = AppConfig.MSG_INVALID_ADMIN_LOGIN_CREDENTIALS;
                    //req.getSession().setAttribute("error", errText);
                    res.sendRedirect("/admin/login?error=" + URLEncoder.encode(errText));
                })
                .defaultSuccessUrl("/admin/dashboard", true)
                .permitAll()
            )
            .exceptionHandling(e -> e
                .accessDeniedPage("/admin/login")
            )

            .exceptionHandling(e -> e
                .defaultAuthenticationEntryPointFor(
                    (req, res, ex) -> {
                        Logger.log("FORCE LOGIN: " + req.getRequestURI());
                        res.sendRedirect("/admin/login");
                    },
                    new AntPathRequestMatcher("/admin/**")
                )
                .authenticationEntryPoint((req, res, ex) -> {
                    Logger.log("NOT AUTHENTICATED: " + req.getRequestURI());

                    String errText = "Du må logge inn";
                    res.sendRedirect("/admin/login?error=" + URLEncoder.encode(errText));
                })
                .accessDeniedHandler((req, res, ex) -> {
                    Logger.log("UNAUTHORIZED ACCESS ATTEMPT: " + req.getRequestURI());

                    String errText = "Uautorisert adgang, prøve å logge inn på ny";
                    //req.getSession().setAttribute("error", errText);
                    res.sendRedirect("/admin/login?error=" + URLEncoder.encode(errText));
                })
            )
            .logout(logout -> logout.logoutUrl("/admin/logout"));

        return http.build();
    }


    /**
     * Defines checking credentials for admin users. In a real application, this should be replaced with a proper user
     * service that fetches user details from a database.
     *
     * @param repo
     * @return
     */

    @Bean
    UserDetailsService users(TenantRepository repo) {
        return username -> {
            // find tenant in public schema
            return SchemaContext.scopeSchema("public", () -> {
                var r = repo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));

                return User.withUsername(r.getEmail())
                    .password(r.getPasswordHash()) // must be encoded!
                    .roles("ADMIN")
                    .build();
            });
        };
    }
}