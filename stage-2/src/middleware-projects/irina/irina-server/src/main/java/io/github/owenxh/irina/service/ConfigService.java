package io.github.owenxh.irina.service;

import io.github.owenxh.irina.event.ConfigChangedEvent;
import io.github.owenxh.irina.model.*;
import io.github.owenxh.irina.repository.ConfigRepository;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

/**
 * Config service
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@Slf4j
@Service
public class ConfigService implements ApplicationEventPublisherAware {

    private final ConfigRepository repository;
    private final MapperFacade beanMapper;
    private ApplicationEventPublisher eventBus;

    public ConfigService(ConfigRepository repository, MapperFacade beanMapper) {
        this.repository = repository;
        this.beanMapper = beanMapper;
    }

    @Override
    public void setApplicationEventPublisher(@Nonnull ApplicationEventPublisher applicationEventPublisher) {
        this.eventBus = applicationEventPublisher;
    }

    public void save(Config request) {
        Config config = beanMapper.map(request, Config.class);
        // validate type format by unmarshall
        config.unmarshall();

        Config old = repository.get(request.getDataId());
        config = repository.save(config);
        ConfigChangedEvent event = ConfigChangedEvent.configChangedEvent(old, config);
        eventBus.publishEvent(event);
    }

    public void delete(String dataId) {
        Config config = repository.delete(dataId);
        if (config != null) {
            eventBus.publishEvent(ConfigChangedEvent.configRemoved(config));
        }
    }

    public Config get(String dataId) {
        return repository.get(dataId);
    }
}
