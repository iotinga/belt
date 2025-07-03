package io.tinga.b3.core.shadowing.operation;

import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3Topic;
public record Operation<M extends B3Message<?>>(B3Topic sourceTopic, M message) {
    
}
