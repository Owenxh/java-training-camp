package io.github.owenxh.irina.client;

import io.github.owenxh.irina.model.Config;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Config wrapper
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@Getter
@ToString
public class ConfigWrapper implements Supplier<Map<String, String>> {

    public static final ConfigWrapper EMPTY = new ConfigWrapper(null);

    private final Config source;
    private final Map<String, String> config;

    private ConfigWrapper(Config source) {
        this.source = source;
        this.config = source == null ? Collections.emptyMap() : source.unmarshall();
    }

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(config);
    }

    @Override
    public Map<String, String> get() {
        return config;
    }

    public static ConfigWrapper of(Config config) {
        return new ConfigWrapper(config);
    }
}
