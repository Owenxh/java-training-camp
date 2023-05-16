package io.github.owenxh.irina.server.controller;

import com.google.common.collect.Sets;
import io.github.owenxh.irina.model.ConfigChangedEvent;
import io.github.owenxh.irina.server.properties.IrinaServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Watch config controller
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@Slf4j
@RestController
public class WatchConfigController {

    public static final ResponseEntity<?> NOT_MODIFIED = ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();

    private final IrinaServerProperties serverProperties;
    private final BlockingQueue<ConfigChangedEvent> configChangedEvents = new LinkedBlockingQueue<>();
    private final Map<String, Set<Consumer<ConfigChangedEvent>>> configChangedListeners = new ConcurrentHashMap<>();
    private final Object[] locks;

    public WatchConfigController(IrinaServerProperties properties) {
        this.serverProperties = properties;
        Object[] locks = new Object[16];
        for (int i = 0; i < locks.length; i++) {
            locks[i] = new Object();
        }
        this.locks = locks;
    }

    @PostConstruct
    public void init() {
        Runnable task = () -> {
            for (; ; ) {
                final ConfigChangedEvent event;
                try {
                    event = configChangedEvents.poll(getPollConfigChangeTimeout(), TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                if (event == null) {
                    continue;
                }
                Set<Consumer<ConfigChangedEvent>> listeners = configChangedListeners.remove(event.getDataId());
                if (CollectionUtils.isEmpty(listeners)) {
                    continue;
                }
                for (Consumer<ConfigChangedEvent> listener : listeners) {
                    listener.accept(event);
                }
            }
        };
        Thread notificationThread = new Thread(task, "config-notification");
        notificationThread.setDaemon(true);
        notificationThread.start();
    }

    @GetMapping("/config/watch")
    public DeferredResult<ResponseEntity<ConfigChangedEvent>> watchConfig(@RequestParam(name = "dataId") String dataId) {
        final DeferredResult<ResponseEntity<ConfigChangedEvent>> deferredResult = new DeferredResult<>(getWatchTimeoutMills(), NOT_MODIFIED);
        final Consumer<ConfigChangedEvent> callback = event -> deferredResult.setResult(ResponseEntity.ok(event));

        // remove listener when timeout
        deferredResult.onTimeout(() ->
            this.configChangedListeners.computeIfPresent(dataId, (key, listeners) -> {
                listeners.remove(callback);
                return listeners;
            })
        );

        // add listener
        synchronized (this.locks[dataId.hashCode() % this.locks.length]) {
            Set<Consumer<ConfigChangedEvent>> listeners = this.configChangedListeners.get(dataId);
            if (listeners == null) {
                listeners = Sets.newConcurrentHashSet();
            }
            listeners.add(callback);
            this.configChangedListeners.put(dataId, listeners);
        }

        return deferredResult;
    }

    @EventListener
    public void onConfigChangedEvent(ConfigChangedEvent event) {
        try {
            this.configChangedEvents.put(event);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private long getWatchTimeoutMills() {
        return serverProperties.getWatch().getTimeoutMills();
    }

    private long getPollConfigChangeTimeout() {
        Duration duration = serverProperties.getWatch().getPollConfigChangeTimeout();
        return duration.getSeconds();
    }

}
