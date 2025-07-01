package io.tinga.b3.protocol.topic;

import io.tinga.b3.protocol.TopicNameValidationException;

public interface B3TopicRoot {
    B3Topic agent(String id) throws TopicNameValidationException;

    B3Topic entity(String id) throws TopicNameValidationException;
}
