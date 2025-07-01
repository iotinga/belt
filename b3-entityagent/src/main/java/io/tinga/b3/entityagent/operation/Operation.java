package io.tinga.b3.entityagent.operation;

import io.tinga.b3.protocol.GenericB3Message;

public record Operation(String desiredTopic, GenericB3Message message, String reportedTopic, String role) {
    
}
