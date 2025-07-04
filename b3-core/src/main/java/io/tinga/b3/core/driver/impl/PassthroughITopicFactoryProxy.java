package io.tinga.b3.core.driver.impl;

import com.google.inject.Inject;

import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.protocol.B3Topic;
import it.netgrid.bauer.ITopicFactory;
import it.netgrid.bauer.Topic;

public class PassthroughITopicFactoryProxy implements ITopicFactoryProxy {

    private final ITopicFactory factory;

    @Inject
    public PassthroughITopicFactoryProxy(ITopicFactory factory) {
        this.factory = factory;
    }

    @Override
    public <E> Topic<E> getTopic(B3Topic topic, boolean retained) {
        return this.factory.getTopic(topic.toString(retained));
    }

}