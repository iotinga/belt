package io.tinga.b3.protocol.topic;

import io.tinga.b3.protocol.TopicNameValidationException;


public interface RootTopic {
    AgentTopic agent(String id) throws TopicNameValidationException;
    EntityTopic entity(String id) throws TopicNameValidationException;
}
