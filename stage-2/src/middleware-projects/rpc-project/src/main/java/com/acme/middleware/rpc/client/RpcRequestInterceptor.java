package com.acme.middleware.rpc.client;

import com.acme.middleware.rpc.InvocationRequest;
import org.jetbrains.annotations.NotNull;

/**
 * RPC request interceptor
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@FunctionalInterface
public interface RpcRequestInterceptor {

    Object intercept(@NotNull InvocationRequest request, @NotNull RpcRequestExecution execution);
}
