package io.github.owenxh.irina.model;

import com.alibaba.fastjson2.TypeReference;
import com.google.common.collect.Maps;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Type of the config content
 *
 * @author Owen.Yuan
 * @since 1.0
 */
public enum Type {

    JSON {
        @Override
        public Map<String, String> unmarshall(@NonNull String content) {
            return com.alibaba.fastjson2.JSON.parseObject(content, new TypeReference<Map<String, String>>() {});
        }

        @Override
        public String marshall(@NonNull Map<String, String> kvs) {
            return com.alibaba.fastjson2.JSON.toJSONString(kvs);
        }
    },

    YAML {
        @Override
        public Map<String, String> unmarshall(@NonNull String content) {
            Yaml yaml = new Yaml();
            return yaml.load(content);
        }

        @Override
        public String marshall(@NonNull Map<String, String> kvs) {
            Yaml yaml = new Yaml();
            return yaml.dumpAsMap(kvs);
        }
    },

    PROPERTIES {
        @Override
        public Map<String, String> unmarshall(@NonNull String content) throws IOException {
            Properties properties = PropertiesLoaderUtils
                    .loadProperties(new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8)));
            return Maps.fromProperties(properties);
        }

        @Override
        public String marshall(@NonNull Map<String, String> kvs) {
            if (CollectionUtils.isEmpty(kvs)) {
                return "";
            }
            StringBuilder builder = new StringBuilder(kvs.size() * 16);
            for (Map.Entry<String, String> kv : kvs.entrySet()) {
                builder.append(kv.getKey())
                        .append('=')
                        .append(kv.getValue())
                        .append('\n');
            }
            return builder.toString();
        }
    };

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
        return this.name().equalsIgnoreCase(type);
    }

    public abstract Map<String, String> unmarshall(@NonNull String content) throws IOException;

    public abstract String marshall(@NonNull Map<String, String> kvs) throws IOException;

}
