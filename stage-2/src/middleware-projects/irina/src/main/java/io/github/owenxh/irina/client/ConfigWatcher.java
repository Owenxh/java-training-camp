package io.github.owenxh.irina.client;

import io.github.owenxh.irina.model.Config;
import io.github.owenxh.irina.model.ConfigChangedEvent;
import io.github.owenxh.irina.model.Operation;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Config watcher
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@Slf4j
public class ConfigWatcher implements Runnable, LifeCycle {

    private final IrinaTemplate irinaTemplate;
    private final Predicate<Config> validator;
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
            irinaTemplate.getApiClient()
                    .watch(dataId)
                    .ifPresent(this::onConfigChangedEvent);
        }
    }

    protected void onConfigChangedEvent(ConfigChangedEvent event) {
        final Config config = event.getConfig();
        if (!this.validator.test(config)) {
            return;
        }

        // local config cache
        ConfigWrapper old = irinaTemplate.getConfigCache(dataId);

        // last config from config server
        ConfigWrapper newly = ConfigWrapper.of(config);

        ConfigChangedEntryList entries;
        if (Operation.DELETE.equals(event.getOperation())) {
            entries = ConfigChangedEntryList.fromDeleted(old);
            irinaTemplate.removeConfigCache(dataId);
        } else {
            entries = ConfigChangedEntryList.difference(old, newly);
            irinaTemplate.putConfigCache(dataId, newly);
        }
        // notify changed kvs
        entries.fire(listener);
    }
}
