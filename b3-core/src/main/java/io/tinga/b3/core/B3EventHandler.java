package io.tinga.b3.core;

import io.tinga.b3.protocol.topic.B3Topic;

public interface B3EventHandler<E> {
    public String getName();

    public boolean handle(B3Topic topic, E event) throws Exception;
}
