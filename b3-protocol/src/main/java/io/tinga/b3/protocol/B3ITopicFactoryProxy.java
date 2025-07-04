package io.tinga.b3.protocol;

import it.netgrid.bauer.Topic;

public interface B3ITopicFactoryProxy {
   <E> Topic<E> getTopic(B3Topic topic, boolean retained);
}
