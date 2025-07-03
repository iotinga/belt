package io.tinga.b3.core.shadowing.operation;

import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3Topic;

public interface OperationGrantsChecker<M extends B3Message<?>> {
    void bindTo(B3Topic.Name topicRoot);
    boolean isAllowed(Operation<M> operation);
}
