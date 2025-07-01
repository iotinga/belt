package io.tinga.b3.entityagent.operation;

import io.tinga.b3.protocol.GenericB3Message;

public class EntityOperationTopicBasedFactory implements EntityOperationFactory {

    @Override
    public EntityOperation buildFrom(String topic, GenericB3Message message) throws InvalidEntityOperationException {
        int lastSlashIndex = topic.lastIndexOf('/');

        if(lastSlashIndex == -1 || lastSlashIndex == topic.length() -1) {
            throw new InvalidEntityOperationException();
        }
        String reportedTopic = topic.substring(0, lastSlashIndex);
        String role = topic.substring(lastSlashIndex + 1);
        return new EntityOperation(topic, message, reportedTopic, role);
    }
    
}