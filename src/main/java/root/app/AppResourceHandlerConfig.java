package root.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import root.includes.logger.logger.Logger;

/**
 * TODO: this file only works on localhost, it messes with paths on TomEe, must fix
 */

@Configuration
public class AppResourceHandlerConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String path = System.getProperty("user.dir"); // project root

        registry
            .addResourceHandler("/css/**")
            .addResourceLocations("file:" + path + "/src/main/resources/static/css/");

        registry
            .addResourceHandler("/js/**")
            .addResourceLocations("file:" + path + "/src/main/resources/static/js/");

        Logger.log("added resource handler, " + path + "/css/**");
        Logger.log("added resource handler, " + path + "/js/**");
    }
}