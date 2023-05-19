package io.github.owenxh.irina.client;

import io.github.owenxh.irina.model.Config;
import io.github.owenxh.irina.model.ConfigChangedEvent;
import io.github.owenxh.irina.model.Type;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Config APIs client
 *
 * @author Owen.Yuan
 * @since 1.0
 */
public interface ConfigApi {

    void save(Config config);

    Optional<Config> get(String dataId);

    Optional<Config> get(String dataId, @Nullable Type type);

    void remove(String dataId);

    Optional<ConfigChangedEvent> watch(String dataId);

}
