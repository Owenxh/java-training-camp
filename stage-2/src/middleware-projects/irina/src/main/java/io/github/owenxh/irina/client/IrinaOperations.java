package io.github.owenxh.irina.client;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Irina operations
 *
 * @author Owen.Yuan
 * @since 1.0
 */
public interface IrinaOperations {

    Map<String, String> getConfig(String dataId);

    LifeCycle watchConfig(String dataId, Consumer<ConfigChangedEntryList> fn);

}
