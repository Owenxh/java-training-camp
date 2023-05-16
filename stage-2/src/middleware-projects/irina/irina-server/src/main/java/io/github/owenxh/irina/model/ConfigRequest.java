package io.github.owenxh.irina.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Create or update config request.
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigRequest {

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
}
