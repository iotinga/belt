package io.tinga.b3.core.protocol;

import io.tinga.b3.protocol.topic.B3TopicFactory;
import io.tinga.b3.protocol.topic.B3TopicFactoryImpl;

public class TestB3TopicFactory extends B3TopicFactoryImpl {

    private static final TestB3TopicFactory instance = new TestB3TopicFactory();
    
    public static B3TopicFactory instance() {
        return instance;
    }

}
