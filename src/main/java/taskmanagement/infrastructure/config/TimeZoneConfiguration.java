package taskmanagement.infrastructure.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Slf4j
@Configuration
public class TimeZoneConfiguration {

    @Value("${server.timeZone}")
    private TimeZone timeZone;

    @PostConstruct
    public void init() {
        TimeZone.setDefault(timeZone);
        log.info("Set default timeZone to: {}", timeZone.getID());
    }

}
