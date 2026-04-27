package root.includes;

import jakarta.servlet.ServletContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import root.includes.logger.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.io.File;

/**
 * Utility class written by chatgpt to just collect all .js files and add client-review.js as last file if it
 * exists.
 */

public class JsLoader {

    public static List<String> findFiles() throws Exception {
        var resolver = new PathMatchingResourcePatternResolver();

        Resource[] resources = resolver.getResources("classpath:/static/js/**/*.js");

        List<String> files = new ArrayList<>();

        for (Resource r : resources) {
            String path = r.getURL().toString();

            // extract /js/... part
            int idx = path.indexOf("/static/");
            if (idx != -1) {
                files.add(path.substring(idx + "/static".length()));
            }
        }

        // sort + force last
        files.sort((a, b) -> {
            if (a.endsWith("client-review.js")) return 1;
            if (b.endsWith("client-review.js")) return -1;
            return a.compareTo(b);
        });

        return files;
    }

    public static void compileFile(String filename) {
        try {
            String userDir = System.getProperty("user.dir");
            Path path = java.nio.file.Paths.get(userDir, filename);

            if(Files.deleteIfExists(path)){
                Logger.log("Old generated javascript file deleted");
            }

            var scripts = JsLoader.findFiles();

            StringBuilder sb = new StringBuilder(1_000_000);
            for (String src : scripts) {
                String cp = "static" + src;

                try (var is = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(cp)) {

                    //if (is == null) continue;

                    String sFile = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                    sb.append("/* BEGIN FILE " + src + " */\n");
                    sb.append(sFile);
                    sb.append("\n\n");
                }
            }

            java.nio.file.Files.writeString(
                path,
                sb.toString(),
                java.nio.charset.StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}