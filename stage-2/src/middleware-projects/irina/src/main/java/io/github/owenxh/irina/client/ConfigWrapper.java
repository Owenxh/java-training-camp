package io.github.owenxh.irina.client;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.owenxh.irina.model.Config;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
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

    public static ConfigChangedEntryList extractConfigChangedEntries(@Nullable ConfigWrapper old, @Nonnull ConfigWrapper that) {
        Set<ConfigChangedEntry> result = Sets.newTreeSet();
        if (old == null) {
            that.getConfig().forEach((key, value) -> result.add(changedEntry(key, null, value, that.source)));
            return new ConfigChangedEntryList(result);
        }

        MapDifference<String, String> difference = Maps.difference(old.config, that.config);
        difference.entriesDiffering()
                .forEach((key, diff) -> result.add(changedEntry(key, diff.leftValue(), diff.rightValue(), that.source)));
        difference.entriesOnlyOnLeft()
                .forEach((key, value) -> result.add(changedEntry(key, value, null, that.source)));
        difference.entriesOnlyOnRight()
                .forEach((key, value) -> result.add(changedEntry(key, null, value, that.source)));
        return new ConfigChangedEntryList(result);
    }

    private static ConfigChangedEntry changedEntry(String key, String oldValue, String value, Config newConfig) {
        return ConfigChangedEntry.builder()
                .key(key)
                .oldValue(oldValue)
                .value(value)
                .revision(newConfig.getRevision())
                .build();
    }
}
