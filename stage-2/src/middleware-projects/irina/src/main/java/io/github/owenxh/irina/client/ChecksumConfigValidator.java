package io.github.owenxh.irina.client;

import io.github.owenxh.irina.model.Checksum;
import io.github.owenxh.irina.model.Config;

import java.util.function.Function;

/**
 * Checksum config validator
 *
 * @author Owen.Yuan
 * @since 1.0
 */
public class ChecksumConfigValidator implements Function<Config, Boolean> {

    public static final ChecksumConfigValidator INSTANCE = new ChecksumConfigValidator();

    @Override
    public Boolean apply(Config config) {
        if (config == null) {
            return true;
        }
        Checksum checksum = config.getChecksum();
        return checksum != null && checksum.matches(config::contentAsBytes);
    }
}
