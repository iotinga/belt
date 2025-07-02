package io.tinga.b3.protocol.topic;

public class TestB3TopicFactory extends B3TopicFactoryImpl {

    private static final TestB3TopicFactory instance = new TestB3TopicFactory();
    
    public static B3TopicFactory instance() {
        return instance;
    }

}
