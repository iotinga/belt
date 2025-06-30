package io.tinga.b3.entityagent.operation;

public record EntityOperation(String desiredTopic, EntityMessage message, String reportedTopic, String role) {
    
}
