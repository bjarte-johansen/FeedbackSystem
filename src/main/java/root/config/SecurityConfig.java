package root.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import root.app.AppRequestSchema;
import root.repositories.TenantRepository;

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
                //.requestMatchers("/favicon.ico").permitAll()
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")
                // 👇 PUT IT HERE
                .successHandler((req, res, auth) -> {
                    System.out.println("LOGIN OK: " + auth.getName());
                    res.sendRedirect("/admin/dashboard");
                })
                .failureHandler((req, res, ex) -> {
                    System.out.println("LOGIN FAIL: " + ex.getMessage());
                    res.sendRedirect("/admin/login");
                })
                .defaultSuccessUrl("/admin/dashboard", true)
                .permitAll()
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
            try (var _ = AppRequestSchema.withThreadSchema("public")) {
                var r = repo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));

                return User.withUsername(r.getEmail())
                    .password(r.getPasswordHash()) // must be encoded!
                    .roles("ADMIN")
                    .build();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}