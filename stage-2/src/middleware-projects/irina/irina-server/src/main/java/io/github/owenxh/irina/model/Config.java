package io.github.owenxh.irina.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the user config model.
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Config {

    /**
     * Represents the unique ID of the config.
     */
    String dataId;

    /**
     * Represents the content of the config.
     */
    String content;

    /**
     * The type of the {@link #content}
     */
    Type type;

    /**
     * Last version of the config.
     */
    long revision;

    /**
     * Represents the last update time of the config.
     */
    long lastUpdateTime;

    /**
     * The checksum of the content.
     */
    Checksum checksum;

    /**
     * Apply the checksum algorithm
     * @param algorithm the hash algorithm
     */
    public void checksum(Algorithm algorithm) {
        this.checksum = Checksum.of(algorithm, () -> content.getBytes(StandardCharsets.UTF_8));
    }

    private Config(Config original) {
        this.dataId  = original.dataId;
        this.content = original.content;
        this.type = original.type;
        this.revision = original.revision;
        this.lastUpdateTime = original.lastUpdateTime;
        this.checksum = original.checksum;
    }

    /**
     * Parses the config {@link #content} to key-values.
     * @return the key-values.
     */
    public Map<String, String> unmarshall() {
        Objects.requireNonNull(this.type);
        Objects.requireNonNull(this.content);
        try {
           return this.type.unmarshall(this.content);
        } catch (Exception e) {
            throw new RuntimeException("Parse context error", e);
        }
    }

    /**
     * Transformer config {@link #content} to another type formatted.
     * @param targetType the target type
     * @return transformed config
     */
    public Config transform(Type targetType) {
        Objects.requireNonNull(targetType);
        if (Objects.equals(this.type, targetType)) {
            return this;
        }

        Config result = new Config(this);
        result.type = targetType;
        result.content = targetType.marshall(unmarshall());
        result.refreshChecksum();
        return result;
    }

    /**
     * Recalculate checksum.
     */
    public void refreshChecksum() {
        if (checksum != null && checksum.getAlgorithm() != null) {
            checksum(checksum.getAlgorithm());
        }
    }
}
