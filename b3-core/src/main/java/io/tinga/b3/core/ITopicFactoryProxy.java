package io.tinga.b3.core;

import io.tinga.b3.protocol.topic.B3Topic;
import it.netgrid.bauer.Topic;

public interface ITopicFactoryProxy {
   <E> Topic<E> getTopic(B3Topic.Name topicRoot, boolean retained);
}
