package root;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import root.ai.GPT;
import root.database.DBTest;

import jakarta.annotation.PostConstruct;
import root.logger.Logger;

import java.io.InputStream;
import java.util.*;

@SpringBootApplication
public class App{
    public static class PropertyFile{
        private Properties props = new Properties();

        public PropertyFile() throws Exception {
        }

        public PropertyFile(String name) throws Exception {
            load(name);
        }

        public void load(String name){
            try (InputStream is = App.class
                .getClassLoader()
                .getResourceAsStream(name)) {

                props.load(is);
            }catch(Exception e) {
                Logger.error("Failed to load property file: " + name, e);
                throw new RuntimeException(e);
            }
        }

        public boolean hasProperty(String key) {
            return props.containsKey(key);
        }
        public String getProperty(String key) {
            return props.getProperty(key);
        }
        public String getProperty(String key, String defaultVal) {
            return props.getProperty(key, defaultVal);
        }
        public Set<String> stringPropertyNames() {
            return props.stringPropertyNames();
        }
        public Set<String> sortedStringPropertyNames() {
            Set<String> keys = new TreeSet<>(props.stringPropertyNames());
            return keys;
        }
    }

    public static void showApplicationProperties() throws Exception{
        var props = new PropertyFile("application.properties");

        try(var ignore = Logger.scope("Loaded application.properties:")) {
            List<String> keys = new ArrayList<>(props.sortedStringPropertyNames());

            for (String key : keys) {
                String val = (String) props.getProperty(key);
                Logger.logf("Property: %s = %s", key, val);
            }
        }

        {
            var appProfileProps = new PropertyFile("application-" + props.getProperty("active.profile")+ ".properties");
            List<String> keys = new ArrayList<>(appProfileProps.sortedStringPropertyNames());

            for (String key : keys) {
                String val = (String) appProfileProps.getProperty(key);
                Logger.logf("Property: %s = %s", key, val);
            }
        }
    }
    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(App.class);
        app.setDefaultProperties(Map.of(
            "spring.profiles.active", "test"
        ));

        app.run(args);
    }

    @Bean
    CommandLineRunner showSources(ConfigurableApplicationContext ctx) {
        return args -> {
            /*
            var env = ctx.getEnvironment();
            var sources = ((org.springframework.core.env.AbstractEnvironment) env)
                .getPropertySources();

            for (var s : sources) {
                System.out.println("SOURCE: " + s.getName());
            }

            System.out.println("ActiveProfiles: " + Arrays.toString(env.getActiveProfiles()));
             */
        };
    }

    @Bean
    CommandLineRunner dbTestRunner() {
        return args -> {
            //showApplicationProperties();

            DBTest.clean();
            DBTest.run();
        };
    }
}