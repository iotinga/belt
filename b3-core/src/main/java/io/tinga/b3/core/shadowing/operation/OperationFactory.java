package io.tinga.b3.core.shadowing.operation;

import io.tinga.b3.protocol.B3Message;
public interface OperationFactory {
    public <M extends B3Message<?>> Operation<M> buildFrom(String topic, M message) throws InvalidOperationException;
}
