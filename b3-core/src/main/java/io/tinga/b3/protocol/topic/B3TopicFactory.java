package io.tinga.b3.protocol.topic;

import io.tinga.b3.protocol.TopicNameValidationException;

public interface B3TopicFactory {
    B3Topic.Name parse(String topicPath) throws TopicNameValidationException;

    B3Topic agent(String id) throws TopicNameValidationException;

    B3Topic entity(String id) throws TopicNameValidationException;
}
