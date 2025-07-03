package io.tinga.b3.core.shadowing.operation;

import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3TopicRoot;
public record Operation<M extends B3Message<?>>(B3TopicRoot.Name sourceTopicName, M message) {
    
}
