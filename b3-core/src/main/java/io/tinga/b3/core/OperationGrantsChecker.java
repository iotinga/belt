package io.tinga.b3.core;

import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3TopicRoot;

public interface OperationGrantsChecker<M extends B3Message<?>> {
    void bindTo(B3TopicRoot.Name topicRoot);
    boolean isAllowed(Operation<M> operation);
}
