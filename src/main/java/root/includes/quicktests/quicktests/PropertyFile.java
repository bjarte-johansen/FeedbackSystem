package root.includes.quicktests.quicktests;

import root.App;
import root.includes.logger.Logger;

import java.io.InputStream;
import java.util.*;

@Deprecated
public class PropertyFile {
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

@Deprecated
class AppPropertiesPrinter {
    public static void showApplicationProperties() throws Exception {
        var props = new PropertyFile("application.properties");

        try (var ignore = Logger.scope("Loaded application.properties:")) {
            List<String> keys = new ArrayList<>(props.sortedStringPropertyNames());

            for (String key : keys) {
                String val = (String) props.getProperty(key);
                Logger.logf("Property: %s = %s", key, val);
            }
        }

        {
            var appProfileProps = new PropertyFile("application-" + props.getProperty("active.profile") + ".properties");
            List<String> keys = new ArrayList<>(appProfileProps.sortedStringPropertyNames());

            for (String key : keys) {
                String val = (String) appProfileProps.getProperty(key);
                Logger.logf("Property: %s = %s", key, val);
            }
        }
    }
}