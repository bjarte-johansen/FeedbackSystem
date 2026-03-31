package root.unused;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Deprecated
@Component
public class DebugStartup {
    @PostConstruct
    public void init() {
        System.out.println("SPRING STARTED");
    }
}
