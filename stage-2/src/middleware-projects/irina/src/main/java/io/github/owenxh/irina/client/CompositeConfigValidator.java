package io.github.owenxh.irina.client;

import io.github.owenxh.irina.model.Config;

import java.util.function.Predicate;

/**
 * Composite config validator
 *
 * @author Owen.Yuan
 * @since 1.0
 */
public class CompositeConfigValidator implements Predicate<Config> {

    private final Predicate<Config> delegate;

    @SafeVarargs
    public CompositeConfigValidator(Predicate<Config>... fns) {
        Predicate<Config> predicate = fns[0];
        for (int i = 1; i < fns.length; i++) {
            predicate = predicate.and(fns[i]);
        }
        this.delegate = predicate;
    }

    @Override
    public boolean test(Config config) {
        return delegate.test(config);
    }
}
