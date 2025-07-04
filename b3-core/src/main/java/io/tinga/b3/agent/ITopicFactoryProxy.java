package io.tinga.b3.agent;

import io.tinga.b3.protocol.B3Topic;
import it.netgrid.bauer.Topic;

public interface ITopicFactoryProxy {
   <E> Topic<E> getTopic(B3Topic topic, boolean retained);
}
