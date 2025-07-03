package io.tinga.b3.protocol.topic;

import io.tinga.b3.protocol.TopicNameValidationException;

public interface B3TopicFactory {
    B3TopicRoot.Name parse(String topicPath) throws TopicNameValidationException;

    B3TopicRoot agent(String id) throws TopicNameValidationException;

    B3TopicRoot entity(String id) throws TopicNameValidationException;
}
