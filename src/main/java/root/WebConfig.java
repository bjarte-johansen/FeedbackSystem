package root;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * TODO: this file only works on localhost, it messes with paths on TomEe, must fix
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String path = System.getProperty("user.dir"); // project root

        registry
            .addResourceHandler("/css/**")
            .addResourceLocations("file:" + path + "/src/main/resources/static/css/");

        registry
            .addResourceHandler("/js/**")
            .addResourceLocations("file:" + path + "/src/main/resources/static/js/");

        System.out.println("file:" + path + "/css/");
        System.out.println("file:" + path + "/js/");
    }
}