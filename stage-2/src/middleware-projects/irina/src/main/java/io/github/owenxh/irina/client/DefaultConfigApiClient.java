package io.github.owenxh.irina.client;

import io.github.owenxh.irina.model.Config;
import io.github.owenxh.irina.model.ConfigChangedEvent;
import io.github.owenxh.irina.model.Type;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

/**
 * Config APIs client
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@Slf4j
public class DefaultConfigApiClient implements ConfigApi {

    private final RestTemplate restTemplate;
    private final String serverEndpoint;
    private final Function<Config, Boolean> configValidator;

    @SafeVarargs
    public DefaultConfigApiClient(String serverEndpoint, Function<Config, Boolean>... configValidators) {
        this.serverEndpoint = serverEndpoint;
        this.restTemplate = new RestTemplate();
        this.configValidator = (configValidators == null || configValidators.length == 0)
                ? config -> true : new CompositeConfigValidator(configValidators);
    }

    private String resolveUrl(String path) {
        return serverEndpoint + path;
    }

    @Override
    public void save(Config config) {
        ResponseEntity<Void> response = restTemplate.postForEntity(resolveUrl("/config"), config, Void.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.warn("Save config failed, config = {}, status = {}", config, response.getStatusCode());
            throw new RuntimeException("Save config failed");
        }
    }

    @Override
    public Optional<Config> get(String dataId) {
        return get(dataId, null);
    }

    @Override
    public Optional<Config> get(String dataId, @Nullable Type type) {
        ResponseEntity<Config> response;
        try {
            response = restTemplate.getForEntity(resolveUrl("/config?dataId={0}&type={1}"), Config.class, dataId, type);
        } catch (HttpClientErrorException e) {
            if (e instanceof HttpClientErrorException.NotFound) {
                return Optional.empty();
            }
            log.warn("Watch config failed, dataId = {}, status = {}", dataId, e.getStatusCode());
            throw new RuntimeException("Watch config failed");
        }
        Config config = response.getBody();
        validateConfig(config);
        return Optional.ofNullable(config);
    }

    @Override
    public void remove(String dataId) {
        restTemplate.delete(resolveUrl("/config?dataId={0}"), dataId);
    }

    @Override
    public Optional<ConfigChangedEvent> watch(String dataId) {
        ResponseEntity<ConfigChangedEvent> response = restTemplate
                .getForEntity(resolveUrl("/config/watch?dataId={0}"), ConfigChangedEvent.class, dataId);
        switch (response.getStatusCode()) {
            case NOT_MODIFIED:
                return Optional.empty();
            case OK:
                if (response.getBody() != null) {
                    validateConfig(response.getBody().getConfig());
                }
                return Optional.ofNullable(response.getBody());
            default:
                log.warn("Watch config failed, dataId = {}, status = {}", dataId, response.getStatusCode());
                throw new RuntimeException("Watch config failed");
        }
    }

    private void validateConfig(Config config) {
        if (config != null && !configValidator.apply(config)) {
            log.warn("Ignore config that validation failed, config = {}", config);
            throw new RuntimeException("config validation failed");
        }
    }

}
