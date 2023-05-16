package io.github.owenxh.irina.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.nio.charset.StandardCharsets;

/**
 * Represents the user config.
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@Getter
@Setter
@ToString
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

    public void applyChecksum(Algorithm algorithm) {
        this.checksum = new Checksum(algorithm, algorithm.hash(this.content.getBytes(StandardCharsets.UTF_8)));
    }

}
