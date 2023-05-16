package io.github.owenxh.irina.client;

import java.util.Collection;
import java.util.Iterator;

/**
 * Config changed entries list
 *
 * @author Owen.Yuan
 * @since 1.0
 */
public class ConfigChangedEntryList implements Iterable<ConfigChangedEntry> {

    private final Collection<ConfigChangedEntry> entries;

    public ConfigChangedEntryList(Collection<ConfigChangedEntry> entries) {
        this.entries = entries;
    }

    @Override
    public Iterator<ConfigChangedEntry> iterator() {
        return entries.iterator();
    }
}
