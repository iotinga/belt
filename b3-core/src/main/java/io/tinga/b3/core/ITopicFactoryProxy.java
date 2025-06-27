package io.tinga.b3.core;

import io.tinga.b3.protocol.topic.TopicName;
import it.netgrid.bauer.Topic;

public interface ITopicFactoryProxy {
   <E> Topic<E> getTopic(TopicName topicName, boolean retained);
}
