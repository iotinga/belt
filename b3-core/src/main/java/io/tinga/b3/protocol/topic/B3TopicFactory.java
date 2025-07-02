package io.tinga.b3.protocol.topic;

import io.tinga.b3.protocol.TopicNameValidationException;

public interface B3TopicFactory {
    public B3Topic.Name parse(String topicPath) throws TopicNameValidationException;
    B3TopicRoot root();
}
