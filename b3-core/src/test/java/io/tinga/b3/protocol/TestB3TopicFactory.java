package io.tinga.b3.protocol;

import io.tinga.b3.protocol.impl.StandardB3TopicFactory;

public class TestB3TopicFactory extends StandardB3TopicFactory {

    private static final TestB3TopicFactory instance = new TestB3TopicFactory();
    
    public static B3Topic.Factory instance() {
        return instance;
    }

}
