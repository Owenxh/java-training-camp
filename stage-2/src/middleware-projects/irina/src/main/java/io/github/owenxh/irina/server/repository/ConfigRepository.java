package io.github.owenxh.irina.server.repository;

import io.github.owenxh.irina.model.*;

/**
 * Config repository
 *
 * @author Owen.Yuan
 * @since 1.0
 */
public interface ConfigRepository {

    Config save(Config config);

    Config delete(String dataId);

    Config get(String dataId);
}
