package root.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import root.includes.logger.Logger;
import root.services.LocalService;

// TODO, check that isLocal is working correctly, and that resources are being served correctly in local vs prod environments

@Configuration
public class AppResourceHandlerConfig implements WebMvcConfigurer {
    @Autowired
    private LocalService localService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if(!localService.isLocal()) {
            Logger.log("not running locally, skipping resource handler config");
            return;
        }

        String path = System.getProperty("user.dir"); // project root

        registry
            .addResourceHandler("/**")
            .addResourceLocations("file:" + path + "/src/main/resources/static/");
   }
}