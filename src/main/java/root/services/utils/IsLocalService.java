package root.services.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class IsLocalService {
    @Autowired
    Environment env;

    /**
     * Determines if the application is running in a local environment. This method checks if the IP address is a
     * loopback address or if the active Spring profiles include "local", "dev", or "local & debug".
     *
     * -Dspring.profiles.active=local must be in VM options in build configuration to make this work, and
     * ONLY when doing testing / dev, not for deployment. For deployment, the active profile should be set
     * to "prod" or something similar, and this method should return false.
     *
     * @return
     */

    public boolean isLocal() {
        return Arrays.asList(env.getActiveProfiles()).contains("local");
    }
}
