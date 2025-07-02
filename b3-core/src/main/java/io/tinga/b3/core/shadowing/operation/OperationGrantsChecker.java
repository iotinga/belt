package io.tinga.b3.core.shadowing.operation;

import io.tinga.b3.protocol.B3Message;

public interface OperationGrantsChecker<M extends B3Message<?>> {
    public boolean isAllowed(Operation<M> operation);
}
