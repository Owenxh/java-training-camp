package com.acme.middleware.rpc.server;

import com.acme.middleware.rpc.InvocationRequest;
import com.acme.middleware.rpc.InvocationResponse;
import org.jetbrains.annotations.NotNull;

/**
 * Rpc server execution
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@FunctionalInterface
public interface RpcServerExecution {

    InvocationResponse execute(@NotNull InvocationRequest request);
}
