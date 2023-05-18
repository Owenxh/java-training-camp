package io.github.owenxh.irina.client;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

/**
 * Default implementation of {@link IrinaOperations}
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@Slf4j
public class IrinaTemplate implements IrinaOperations {

    private final DefaultConfigApiClient apiClient;
    private final Map<String, ConfigWrapper> configCache;
    private final Object[] locks;
    private final ThreadFactory threadFactory;

    public IrinaTemplate(String serverEndpoint) {
        this.apiClient = new DefaultConfigApiClient(serverEndpoint, ChecksumConfigValidator.INSTANCE);
        this.configCache = Maps.newConcurrentMap();
        Object[] locks = new Object[16];
        for (int i = 0; i < locks.length; i++) {
            locks[i] = new Object();
        }
        this.locks = locks;
        this.threadFactory = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("config-watcher-%d")
                .build();
    }

    public ConfigApi getApiClient() {
        return this.apiClient;
    }

    private Object resolveLock(String dataId) {
        return locks[dataId.hashCode() % locks.length];
    }

    @Override
    public Map<String, String> getConfig(String dataId) {
        ConfigWrapper config = getConfigCache(dataId);
        if (config != null) {
            return config.get();
        }
        synchronized (resolveLock(dataId)) {
            config = getConfigCache(dataId);
            if (config != null) {
                return config.get();
            }
            config = apiClient.get(dataId).map(ConfigWrapper::of).orElse(ConfigWrapper.EMPTY);
            if (!config.isEmpty()) {
                putConfigCache(dataId, config);
            }
            return config.get();
        }
    }

    protected ConfigWrapper getConfigCache(String dataId) {
        return configCache.get(dataId);
    }

    protected void putConfigCache(String dataId, ConfigWrapper config) {
        configCache.put(dataId, config);
    }

    @Override
    public LifeCycle watchConfig(String dataId, Consumer<ConfigChangedEntryList> fn) {
        return new ConfigWatcherWrapper(new ConfigWatcher(this, fn, dataId));
    }

    private class ConfigWatcherWrapper implements LifeCycle {

        private final ConfigWatcher worker;
        private final Thread thread;

        public ConfigWatcherWrapper(ConfigWatcher worker) {
            this.worker = worker;
            this.thread = threadFactory.newThread(worker);
        }

        @Override
        public void start() {
            this.worker.start();
            this.thread.start();
        }

        @Override
        public void close() {
            worker.close();
            thread.interrupt();
        }
    }

}
