package io.github.owenxh.irina.client;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.owenxh.irina.model.Config;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Config changed entries list
 *
 * @author Owen.Yuan
 * @since 1.0
 */
public class ConfigChangedEntryList implements Iterable<ConfigChangedEntry> {

    private final Collection<ConfigChangedEntry> entries;

    private ConfigChangedEntryList(Collection<ConfigChangedEntry> entries) {
        this.entries = entries;
    }

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(entries);
    }

    public void fire(Consumer<ConfigChangedEntryList> consumer) {
        if (!isEmpty()) {
            try {
                consumer.accept(this);
            } catch (Throwable ignore) {
            }
        }
    }

    public static ConfigChangedEntryList fromAdded(@Nonnull ConfigWrapper cfg) {
        Set<ConfigChangedEntry> result = Sets.newTreeSet();
        cfg.getConfig().forEach((key, value) -> result.add(changedEntry(key, null, value, cfg.getSource())));
        return new ConfigChangedEntryList(result);
    }

    public static ConfigChangedEntryList fromDeleted(@Nonnull ConfigWrapper cfg) {
        Set<ConfigChangedEntry> result = Sets.newTreeSet();
        cfg.getConfig().forEach((key, value) -> result.add(changedEntry(key, value, null, cfg.getSource())));
        return new ConfigChangedEntryList(result);
    }

    public static ConfigChangedEntryList difference(@Nullable ConfigWrapper old, @Nonnull ConfigWrapper newly) {
        Set<ConfigChangedEntry> result = Sets.newTreeSet();
        if (old == null) {
            return fromAdded(newly);
        }

        MapDifference<String, String> difference = Maps.difference(old.get(), newly.get());
        difference.entriesDiffering()
                .forEach((key, diff) -> result.add(changedEntry(key, diff.leftValue(), diff.rightValue(), newly.getSource())));
        difference.entriesOnlyOnLeft()
                .forEach((key, value) -> result.add(changedEntry(key, value, null, newly.getSource())));
        difference.entriesOnlyOnRight()
                .forEach((key, value) -> result.add(changedEntry(key, null, value, newly.getSource())));
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

    @Override
    public Iterator<ConfigChangedEntry> iterator() {
        return entries.iterator();
    }
}
