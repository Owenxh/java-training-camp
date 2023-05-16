package io.github.owenxh.irina.client;

import io.github.owenxh.irina.model.Config;
import io.github.owenxh.irina.model.ConfigChangedEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Config watcher
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@Slf4j
public class ConfigWatcher implements Runnable, LifeCycle {

    private final IrinaTemplate irinaTemplate;
    private final Function<Config, Boolean> validator;
    private final Consumer<ConfigChangedEntryList> listener;
    private final String dataId;
    private final AtomicBoolean stop;

    public ConfigWatcher(IrinaTemplate irinaTemplate,
                         Consumer<ConfigChangedEntryList> listener,
                         String dataId) {
        this.irinaTemplate = irinaTemplate;
        this.validator = this::validateConfigRevision;
        this.listener = listener;
        this.dataId = dataId;
        this.stop = new AtomicBoolean(false);
    }

    private boolean validateConfigRevision(Config config) {
        ConfigWrapper current = irinaTemplate.getConfigCache(config.getDataId());
        if (current != null && config.getRevision() < current.getSource().getRevision()) {
            log.warn("Ignore the smaller config revision because it isn't a fresh one. config = {}, current revision = {}",
                    config, current.getSource().getRevision());
            return false;
        }
        return true;
    }

    @Override
    public void start() {
        irinaTemplate.getConfig(dataId);
    }

    @Override
    public void close() {
        this.stop.compareAndSet(false, true);
    }

    @Override
    public void run() {
        while (!stop.get()) {
            Optional<ConfigChangedEvent> optional = irinaTemplate.getApiClient().watch(dataId);
            if (!optional.isPresent()) {
                log.debug("config not changed, dataId = {}", dataId);
                continue;
            }
            final Config config = optional.get().getConfig();
            if (!validator.apply(config)) {
                continue;
            }
            ConfigWrapper newly = ConfigWrapper.of(config);
            ConfigWrapper old = irinaTemplate.getConfigCache(dataId);
            ConfigChangedEntryList entries = ConfigWrapper.extractConfigChangedEntries(old, newly);
            try {
                listener.accept(entries);
            } catch (Throwable ignore) {
            }
            irinaTemplate.putConfigCache(dataId, newly);
        }
    }
}
