package io.tinga.b3.core.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.protocol.GenericMessage;

public class GenericAgentProxy extends AbstractAgentProxy<JsonNode, GenericMessage> {

    @Inject
    public GenericAgentProxy(ITopicFactoryProxy topicFactoryProxy) {
        super(topicFactoryProxy);
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public Class<GenericMessage> getEventClass() {
        return GenericMessage.class;
    }

}