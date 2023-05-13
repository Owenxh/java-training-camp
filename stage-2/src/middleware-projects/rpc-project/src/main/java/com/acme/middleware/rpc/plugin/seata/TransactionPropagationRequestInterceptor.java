package com.acme.middleware.rpc.plugin.seata;

import com.acme.middleware.rpc.InvocationRequest;
import com.acme.middleware.rpc.client.RpcRequestExecution;
import com.acme.middleware.rpc.client.RpcRequestInterceptor;
import io.seata.core.context.RootContext;
import io.seata.core.model.BranchType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Seata transaction propagation {@link RpcRequestInterceptor}
 *
 * {@link <a href="https://github.com/seata/seata/blob/2.x/integration/dubbo-alibaba/src/main/java/io/seata/integration/dubbo/alibaba/AlibabaDubboTransactionPropagationFilter.java">...</a>}
 *
 * @author Owen.Yuan
 * @since 1.0
 */
public class TransactionPropagationRequestInterceptor implements RpcRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TransactionPropagationRequestInterceptor.class);

    @Override
    public Object intercept(@NotNull InvocationRequest request, @NotNull RpcRequestExecution execution) {
        final MetadataWrapper metadata = MetadataWrapper.wrap(request);

        String xid = RootContext.getXID();
        BranchType branchType = RootContext.getBranchType();

        String rpcXid = metadata.getRpcXid();
        String rpcBranchType = metadata.get(RootContext.KEY_BRANCH_TYPE);

        if (log.isDebugEnabled()) {
            log.debug("xid in RootContext[{}] xid in RpcContext[{}]", xid, rpcXid);
        }

        boolean bind = false;
        if (xid != null) {
            metadata.set(RootContext.KEY_XID, xid);
            metadata.set(RootContext.KEY_BRANCH_TYPE, branchType.name());
        }
        else {
            if (rpcXid != null) {
                RootContext.bind(rpcXid);
                if (Objects.equals(BranchType.TCC.name(), rpcBranchType)) {
                    RootContext.bindBranchType(BranchType.TCC);
                }
                bind = true;
                if (log.isDebugEnabled()) {
                    log.debug("bind xid [{}] branchType [{}] to RootContext", rpcXid, rpcBranchType);
                }
            }
        }
        try {
            return execution.execute(request);
        } finally {
            if (bind) {
                BranchType previousBranchType = RootContext.getBranchType();
                String unbindXid = RootContext.unbind();
                if (BranchType.TCC.equals(previousBranchType)) {
                    RootContext.unbindBranchType();
                }
                if (log.isDebugEnabled()) {
                    log.debug("unbind xid [{}] branchType [{}] from RootContext", unbindXid, previousBranchType);
                }
                if (!rpcXid.equalsIgnoreCase(unbindXid)) {
                    log.warn("xid in change during RPC from {} to {},branchType from {} to {}", rpcXid, unbindXid,
                            rpcBranchType != null ? rpcBranchType : BranchType.AT,previousBranchType);
                    if (unbindXid != null) {
                        RootContext.bind(unbindXid);
                        log.warn("bind xid [{}] back to RootContext", unbindXid);
                        if (BranchType.TCC.equals(previousBranchType)) {
                            RootContext.bindBranchType(BranchType.TCC);
                            log.warn("bind branchType [{}] back to RootContext", previousBranchType);
                        }
                    }
                }
            }
            metadata.remove(RootContext.KEY_XID);
            metadata.remove(RootContext.KEY_BRANCH_TYPE);
        }
    }

    private static class MetadataWrapper {

        private final InvocationRequest delegate;

        private MetadataWrapper(InvocationRequest request) {
            this.delegate = request;
        }

        public static MetadataWrapper wrap(InvocationRequest request) {
            return new MetadataWrapper(request);
        }

        public String getRpcXid() {
            String rpcXid = get(RootContext.KEY_XID);
            if (rpcXid == null) {
                rpcXid = get(RootContext.KEY_XID.toLowerCase());
            }
            return rpcXid;
        }

        private String resolveKey(String key) {
            return TransactionPropagationRequestInterceptor.class.getName() + '.' + key;
        }

        @SuppressWarnings("unchecked")
        public <T> T get(String key) {
            return (T) delegate.getMetadata().get(resolveKey(key));
        }

        public void set(String key, String value) {
            delegate.getMetadata().put(resolveKey(key), value);
        }

        public void remove(String key) {
            delegate.getMetadata().remove(resolveKey(key));
        }
    }

}
