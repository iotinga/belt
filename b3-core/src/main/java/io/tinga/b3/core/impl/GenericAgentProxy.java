package io.tinga.b3.core.impl;

import com.google.inject.Inject;

import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.protocol.GenericB3Message;

public class GenericAgentProxy extends AbstractAgentProxy<GenericB3Message> {

    @Inject
    public GenericAgentProxy(ITopicFactoryProxy topicFactoryProxy) {
        super(topicFactoryProxy);
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public Class<GenericB3Message> getEventClass() {
        return GenericB3Message.class;
    }

}