package io.github.owenxh.irina.client;

/**
 * Config watcher
 *
 * @author Owen.Yuan
 * @since 1.0
 */
public interface LifeCycle extends AutoCloseable {

    void start();

    @Override
    void close();
}
