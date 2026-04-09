package root.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
public class LocalService {
    @Autowired
    Environment env;

    public boolean isLocal(){
        return env.acceptsProfiles(Profiles.of("local", "dev", "local & debug"));
    }
}
