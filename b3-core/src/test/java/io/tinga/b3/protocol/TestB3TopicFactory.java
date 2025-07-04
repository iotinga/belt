package io.tinga.b3.protocol;

import io.tinga.b3.protocol.impl.B3TopicFactoryImpl;

public class TestB3TopicFactory extends B3TopicFactoryImpl {

    private static final TestB3TopicFactory instance = new TestB3TopicFactory();
    
    public static B3Topic.Factory instance() {
        return instance;
    }

}
