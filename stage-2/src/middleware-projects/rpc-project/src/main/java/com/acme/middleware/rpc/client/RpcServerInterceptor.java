package com.acme.middleware.rpc.client;

import com.acme.middleware.rpc.InvocationRequest;
import org.jetbrains.annotations.NotNull;

/**
 * RPC client interceptor
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@FunctionalInterface
public interface RpcServerInterceptor {

    Object intercept(@NotNull InvocationRequest request, @NotNull RpcClientExecution execution);
}
