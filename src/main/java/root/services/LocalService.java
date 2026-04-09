package root.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;
import root.includes.logger.Logger;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collections;

@Component
public class LocalService {
    @Autowired
    Environment env;

    /**
     * Determines if the application is running in a local environment. This method checks if the IP address is a
     * loopback address or if the active Spring profiles include "local", "dev", or "local & debug".
     *
     * @return
     */
    public boolean isLocal() {
        Logger.log("Active profiles: " + Arrays.toString(env.getActiveProfiles()));

        return Arrays.asList(env.getActiveProfiles()).contains("local");

        //return env.acceptsProfiles(Profiles.of("local", "dev", "local & debug"));
    }
}
