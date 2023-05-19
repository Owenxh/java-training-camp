package io.github.owenxh.irina.server.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Irina server properties
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "irina.server")
public class IrinaServerProperties {

    // config watch
    private Watch watch = new Watch();

    @Getter
    @Setter
    @ToString
    public static class Watch {

        // poll config changed event max timeout
        private Duration pollConfigChangeTimeout = Duration.ofSeconds(30);

        // watch timeout milliseconds
        private long timeoutMills = 30_000;
    }
}
