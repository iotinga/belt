
package io.tinga.b3.agent.security.impl;

import io.tinga.b3.agent.InvalidOperationException;
import io.tinga.b3.agent.security.Operation;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;

public class TopicBasedOperationFactory implements Operation.Factory {

    private final B3Topic.Factory topicFactory;

    public TopicBasedOperationFactory(B3Topic.Factory topicFactory) {
        this.topicFactory = topicFactory;
    }

    @Override
    public <M extends B3Message<?>> Operation<M> buildFrom(String topicPath, M message)
            throws InvalidOperationException {
        B3Topic.Valid topic = this.topicFactory.parse(topicPath);
        return this.buildFrom(topic.build(), message);
    }

    @Override
    public <M extends B3Message<?>> Operation<M> buildFrom(B3Topic topic, M message) throws InvalidOperationException {
        if (topic == null || message == null) {
            throw new InvalidOperationException();
        }
        return new Operation<M>(topic, message);
    }

}