package io.tinga.b3.core.shadowing.operation;

import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3Topic;
public interface OperationFactory {
    public <M extends B3Message<?>> Operation<M> buildFrom(String topic, M message) throws InvalidOperationException;
    public <M extends B3Message<?>> Operation<M> buildFrom(B3Topic.Name topic, M message) throws InvalidOperationException;
}
