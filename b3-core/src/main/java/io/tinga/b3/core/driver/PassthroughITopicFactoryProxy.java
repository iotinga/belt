package io.tinga.b3.core.driver;

import com.google.inject.Inject;

import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.protocol.topic.B3TopicRoot;
import it.netgrid.bauer.ITopicFactory;
import it.netgrid.bauer.Topic;

public class PassthroughITopicFactoryProxy implements ITopicFactoryProxy {

    private final ITopicFactory factory;

    @Inject
    public PassthroughITopicFactoryProxy(ITopicFactory factory) {
        this.factory = factory;
    }

    @Override
    public <E> Topic<E> getTopic(B3TopicRoot.Name topicRoot, boolean retained) {
        return this.factory.getTopic(topicRoot.build().toString(retained));
    }

}