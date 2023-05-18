package io.github.owenxh.irina.client;

import com.google.common.collect.Lists;
import io.github.owenxh.irina.model.Config;

import java.util.List;
import java.util.function.Function;

/**
 * Composite config validator
 *
 * @author Owen.Yuan
 * @since 1.0
 */
public class CompositeConfigValidator implements Function<Config, Boolean> {

    private final List<Function<Config, Boolean>> fns;

    @SafeVarargs
    public CompositeConfigValidator(Function<Config, Boolean>... fns) {
        this.fns = Lists.newArrayList(fns);
    }

    @Override
    public Boolean apply(Config config) {
        for (Function<Config, Boolean> fn : fns) {
            if (!fn.apply(config)) {
                return false;
            }
        }
        return true;
    }
}
