package io.tinga.b3.core.driver.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.protocol.topic.B3TopicRoot;
import it.netgrid.bauer.ITopicFactory;
import it.netgrid.bauer.Topic;

public class SingletonsITopicFactoryProxy implements ITopicFactoryProxy {

    private final Map<String, Topic<?>> cache = new HashMap<>();

    private final ITopicFactory factory;

    @Inject
    public SingletonsITopicFactoryProxy(ITopicFactory factory) {
        this.factory = factory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Topic<E> getTopic(B3TopicRoot.Name topicRoot, boolean retained) {
        String topic = topicRoot.build().toString(retained);
        Topic<?> entry = this.cache.get(topic);
        if(entry != null) {
            return (Topic<E>) entry;
        } else {
            Topic<E> newEntry = this.factory.getTopic(topic);
            this.cache.put(topic, newEntry);
            return newEntry;
        }
    }

}