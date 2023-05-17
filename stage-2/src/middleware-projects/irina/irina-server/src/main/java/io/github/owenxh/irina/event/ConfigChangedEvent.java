package io.github.owenxh.irina.event;

import io.github.owenxh.irina.model.Checksum;
import io.github.owenxh.irina.model.Config;
import io.github.owenxh.irina.model.Operation;
import io.github.owenxh.irina.model.Type;
import lombok.Getter;
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
@ToString
public class ConfigChangedEvent {

    private final Operation operation;
    private final String dataId;
    private final Type type;
    private final String content;
    private final String oldContent;
    private final long revision;
    private final long lastUpdateTime;
    private final Checksum checksum;

    private ConfigChangedEvent(@Nullable Config oldConfig, @Nonnull Config config, @Nonnull Operation operation) {
        this.operation = operation;
        this.dataId = config.getDataId();
        this.type = config.getType();
        this.content = config.getContent();
        this.oldContent = oldConfig == null ? null : oldConfig.getContent();
        this.revision = config.getRevision();
        this.checksum = config.getChecksum();
        this.lastUpdateTime = config.getLastUpdateTime();
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
