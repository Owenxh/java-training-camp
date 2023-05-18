package io.github.owenxh.irina.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Config changed event
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ConfigChangedEvent {

    private String dataId;
    private Operation operation;
    private Config oldConfig;
    private Config config;

    private ConfigChangedEvent(@Nullable Config oldConfig, @Nonnull Config config, @Nonnull Operation operation) {
        this.dataId = config.getDataId();
        this.operation = operation;
        this.oldConfig = oldConfig;
        this.config = config;
    }

    public static ConfigChangedEvent configCreated(Config config) {
        return new ConfigChangedEvent(null, config, Operation.ADD);
    }

    public static ConfigChangedEvent configChangedEvent(Config oldConfig, Config newConfig) {
        if (oldConfig == null) {
            return configCreated(newConfig);
        }
        return new ConfigChangedEvent(oldConfig, newConfig, Operation.UPDATE);
    }

    public static ConfigChangedEvent configRemoved(Config config) {
        return new ConfigChangedEvent(null, config, Operation.DELETE);
    }
}
