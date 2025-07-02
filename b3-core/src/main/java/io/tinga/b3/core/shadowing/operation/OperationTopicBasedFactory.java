package io.tinga.b3.core.shadowing.operation;

import io.tinga.b3.protocol.B3Message;

public class OperationTopicBasedFactory implements OperationFactory {

    @Override
    public <M extends B3Message<?>> Operation<M> buildFrom(String topic, M message) throws InvalidOperationException {
        int lastSlashIndex = topic.lastIndexOf('/');

        if(lastSlashIndex == -1 || lastSlashIndex == topic.length() -1) {
            throw new InvalidOperationException();
        }
        String reportedTopic = topic.substring(0, lastSlashIndex);
        String role = topic.substring(lastSlashIndex + 1);
        return new Operation<M>(topic, message, reportedTopic, role);
    }
    
}