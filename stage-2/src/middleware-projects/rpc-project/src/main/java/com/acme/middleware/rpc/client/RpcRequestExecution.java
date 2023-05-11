package com.acme.middleware.rpc.client;

import com.acme.middleware.rpc.InvocationRequest;
import org.jetbrains.annotations.NotNull;

/**
 * Rpc request invoker
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@FunctionalInterface
public interface RpcRequestExecution {

    Object execute(@NotNull InvocationRequest request);
}
