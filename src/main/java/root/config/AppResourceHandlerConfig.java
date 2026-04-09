package root.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import root.includes.logger.Logger;
import root.services.LocalService;

/**
 * TODO: this file only works on localhost, it messes with paths on TomEe, must fix
 */

@Configuration
public class AppResourceHandlerConfig implements WebMvcConfigurer {
    @Autowired
    private LocalService localService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // only do this routine when running locally
        if(!localService.isLocal()) {
            Logger.log("not running locally, skipping resource handler config");
            return;
        }

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