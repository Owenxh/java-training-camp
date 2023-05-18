package com.acme.middleware.rpc.client;

import com.acme.middleware.rpc.InvocationRequest;
import org.jetbrains.annotations.NotNull;

/**
 * Rpc client execution
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@FunctionalInterface
public interface RpcClientExecution {

    Object execute(@NotNull InvocationRequest request);
}
