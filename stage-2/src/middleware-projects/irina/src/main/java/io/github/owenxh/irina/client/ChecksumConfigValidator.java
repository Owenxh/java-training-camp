package io.github.owenxh.irina.client;

import io.github.owenxh.irina.model.Checksum;
import io.github.owenxh.irina.model.Config;

import java.util.function.Predicate;

/**
 * Checksum config validator
 *
 * @author Owen.Yuan
 * @since 1.0
 */
public class ChecksumConfigValidator implements Predicate<Config> {

    public static final Predicate<Config> INSTANCE = new ChecksumConfigValidator();

    @Override
    public boolean test(Config config) {
        if (config == null) {
            return true;
        }
        Checksum checksum = config.getChecksum();
        return checksum != null && checksum.matches(config::contentAsBytes);
    }
}
