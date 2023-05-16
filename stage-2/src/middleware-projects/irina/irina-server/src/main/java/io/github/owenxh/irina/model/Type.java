package io.github.owenxh.irina.model;

import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Type of the config content
 *
 * @author Owen.Yuan
 * @since 1.0
 */
public enum Type {

    JSON,
    XML;

    private static final Map<String, Type> mappings = new HashMap<>();

    static {
        for (Type type : values()) {
            mappings.put(type.name(), type);
        }
    }

    @Nullable
    public static Type resolve(@Nullable String type) {
        return type != null ? mappings.get(type.toUpperCase()) : null;
    }

    public boolean matches(String type) {
        return this.name().equals(type);
    }

}
