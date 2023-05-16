package io.github.owenxh.irina.service;

import io.github.owenxh.irina.event.ConfigChangedEvent;
import io.github.owenxh.irina.model.*;
import io.github.owenxh.irina.repository.ConfigRepository;
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

    public void save(ConfigRequest request) {
        Config oldConfig = repository.get(request.getDataId());

        // TODO validate content by type
        Config newConfig = repository.save(extraConfig(request));

        ConfigChangedEvent event;
        if (oldConfig != null) {
            event = ConfigChangedEvent.configUpdated(oldConfig, newConfig);
        } else {
            event = ConfigChangedEvent.configCreated(newConfig);
        }
        eventBus.publishEvent(event);
    }

    private Config extraConfig(ConfigRequest request) {
        return beanMapper.map(request, Config.class);
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

//    public Config get(String dataId, String type) {
//        Type resultType = Type.resolve(type);
//        if (resultType == null) {
//            throw new IllegalArgumentException("Invalid type of " + type);
//        }
//
//        Config srcConfig = repository.get(dataId);
//        if (srcConfig == null) {
//            return null;
//        }
//
//        Config result = beanMapper.map(srcConfig, Config.class);
//
//        // required content type is the same as the stored content type
//        if (Objects.equals(result.getType(), resultType)) {
//            return result;
//        }
//
//        // TODO Type converter & checksum calc
//        return result;
//    }
}
