package io.github.owenxh.irina.client;

import lombok.*;

/**
 * config changed entry
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "key")
@Builder
public class ConfigChangedEntry implements Comparable<ConfigChangedEntry> {

    private String key;
    private String oldValue;
    private String value;
    private long revision;

    @Override
    public int compareTo(ConfigChangedEntry that) {
        return this.key.compareTo(that.key);
    }
}
