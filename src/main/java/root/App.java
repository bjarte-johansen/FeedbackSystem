package root;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import root.quicktests.DBTest;

import root.logger.Logger;

import java.io.InputStream;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

class FeedbackRequestContext{
    public static ThreadLocal<Connection> CONN = new ThreadLocal<Connection>();
    public static ThreadLocal<Integer> TENANT_ID = new ThreadLocal<Integer>();
}

@SpringBootApplication
public class App{


    public static class PropertyFile{
        private final Properties props = new Properties();

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

    public static String wrap80(String s) {
        StringBuilder out = new StringBuilder(s.length());
        int col = 0;

        for (String word : s.split(" ")) {
            int len = word.length();

            if (col != 0 && col + 1 + len > 80) {
                out.append('\n');
                col = 0;
            }

            if (col != 0) {
                out.append(' ');
                col++;
            }

            out.append(word);
            col += len;
        }

        return out.toString();
    }


    @Bean
    @Order(0)
    CommandLineRunner startup(){
        return (args) -> {
            String tmp = """
██████╗ ███████╗██╗   ██╗██╗███████╗██╗    ██╗
██╔══██╗██╔════╝██║   ██║██║██╔════╝██║    ██║
██████╔╝█████╗  ██║   ██║██║█████╗  ██║ █╗ ██║ 
██╔══██╗██╔══╝  ╚██╗ ██╔╝██║██╔══╝  ██║███╗██║
██║  ██║███████╗ ╚████╔╝ ██║███████╗╚███╔███╔╝
╚═╝  ╚═╝╚══════╝  ╚═══╝  ╚═╝╚══════╝ ╚══╝╚══╝

Version: 1.0.0, DAT109 Project, 2026
Developed by: Bjarte Johansen, Fahad Ahmed, Marcus Lowenstein, Øyvind Nordeide, 
Prince Nixon Alaoysius, Ahmad Ahmed.
System: ReView Feedback Engine
""";
            // TODO: rette navn i reviewbanneren, jeg skrev de etter hukommelse.

            Logger.log("-".repeat(80));
            Logger.log("-".repeat(80));

            Logger.log("");
            Logger.log("");

            Logger.log(tmp);

            Logger.log("");
            Logger.log("");

            Logger.log("-".repeat(80));
            Logger.log("-".repeat(80));
            Logger.log("-".repeat(80));
            Logger.log("");

            Logger.log(wrap80(RepositoryProxyConstructor.getDeveloperWarningMessages()));

            Logger.log("");
            Logger.log("-".repeat(80));
            Logger.log("-".repeat(80));
            Logger.log("-".repeat(80));
            Logger.log("");
            Logger.log("");
        };
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
    CommandLineRunner dbTestRunner(DBTest dbTest) {
        return args -> {
            //showApplicationProperties();

            dbTest
                .clean();
            dbTest
                .run();
        };
    }
}