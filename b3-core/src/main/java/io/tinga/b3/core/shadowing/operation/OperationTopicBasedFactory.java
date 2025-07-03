package io.tinga.b3.core.shadowing.operation;

import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3TopicRoot;
import io.tinga.b3.protocol.topic.B3Topic;
import io.tinga.b3.protocol.topic.B3TopicFactory;

public class OperationTopicBasedFactory implements OperationFactory {

    private final B3TopicFactory topicFactory;

    public OperationTopicBasedFactory(B3TopicFactory topicFactory) {
        this.topicFactory = topicFactory;
    }

    @Override
    public <M extends B3Message<?>> Operation<M> buildFrom(B3TopicRoot.Name topicName, M message)
            throws InvalidOperationException {
        if (topicName == null || message == null) {
            throw new InvalidOperationException();
        }
        return new Operation<M>(topicName.build(), message);
    }

    @Override
    public <M extends B3Message<?>> Operation<M> buildFrom(String topicPath, M message)
            throws InvalidOperationException {
        B3TopicRoot.Name topicRoot = this.topicFactory.parse(topicPath);
        return this.buildFrom(topicRoot, message);
    }

    @Override
    public <M extends B3Message<?>> Operation<M> buildFrom(B3Topic topic, M message) throws InvalidOperationException {
        if (topic == null || message == null) {
            throw new InvalidOperationException();
        }
        return new Operation<M>(topic, message);
    }

}