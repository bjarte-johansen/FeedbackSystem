/**
 *
 * IMPORTANT: To enable localhost development profile, add the following line to run config VM options:
 *      -Dspring.profiles.active=local to VM options (run configuration)
 * IMPORTANT: to enable any schema, set the schema in AppRequestSchema before any database access is made, for example
 * by adding the following line to the beginning of the startup method:
 * */


package root;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


/**
 * Main classes for spring project
 */

@SpringBootApplication
public class App extends SpringBootServletInitializer {

    /**
     * starts spring app on localserver
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.run(args);
    }

    /**
     * used when deployed as WAR in Tomcat
     */

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(App.class);
    }
}