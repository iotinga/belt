package io.tinga.b3.entityagent.operation;

import io.tinga.b3.protocol.GenericB3Message;

public class OperationTopicBasedFactory implements OperationFactory {

    @Override
    public Operation buildFrom(String topic, GenericB3Message message) throws InvalidOperationException {
        int lastSlashIndex = topic.lastIndexOf('/');

        if(lastSlashIndex == -1 || lastSlashIndex == topic.length() -1) {
            throw new InvalidOperationException();
        }
        String reportedTopic = topic.substring(0, lastSlashIndex);
        String role = topic.substring(lastSlashIndex + 1);
        return new Operation(topic, message, reportedTopic, role);
    }
    
}