package io.tinga.b3.core.shadowing.operation;

import io.tinga.b3.protocol.B3Message;
public record Operation<M extends B3Message<?>>(String desiredTopic, M message, String reportedTopic, String role) {
    
}
