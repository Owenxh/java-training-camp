package io.github.owenxh.irina.repository;

import io.github.owenxh.irina.model.Algorithm;
import io.github.owenxh.irina.model.Config;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In memory implementation of {@link ConfigRepository}
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@Component
public class InMemoryConfigRepository implements ConfigRepository {

    private final ConcurrentHashMap<String, Config> cache = new ConcurrentHashMap<>();

    private final AtomicLong revision = new AtomicLong();

    @Override
    public Config save(Config config) {
        config.setRevision(revision.getAndIncrement());
        config.setLastUpdateTime(System.currentTimeMillis());
        config.checksum(Algorithm.SHA256);
        cache.put(config.getDataId(), config);
        return config;
    }

    @Override
    public Config delete(String dataId) {
        return cache.remove(dataId);
    }

    @Override
    public Config get(String dataId) {
        return cache.get(dataId);
    }
}
