package io.tinga.b3.helpers.proxy;

import io.tinga.b3.helpers.AgentProxy;
import io.tinga.b3.protocol.B3ITopicFactoryProxy;
import io.tinga.b3.protocol.B3Message;
import java.util.Random;

public class RandomizedTopicBasedAgentProxy<M extends B3Message<?>> extends StaticTopicBasedAgentProxy<M> {

    public static final int RANDOM_PART_SIZE = 6;
    private static final Random RANDOM_SEED = new Random();
    private static final String RANDOM_GLYPHS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int RANDOM_GLYPHS_COUNT = RANDOM_GLYPHS.length();

    public RandomizedTopicBasedAgentProxy(Config config, Class<M> messageClass, B3ITopicFactoryProxy topicFactoryProxy,
            io.tinga.b3.protocol.B3Topic.Factory topicFactory) {
        super(config, messageClass, topicFactoryProxy, topicFactory);
    }

    @Override
    public String getName() {
        return String.format("%s-%s-%s", config.agentId(), AgentProxy.class.getSimpleName(), randomized(RANDOM_PART_SIZE));
    }

    private static String randomized(int lunghezza) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < lunghezza; i++) {
            int index = RANDOM_SEED.nextInt(RANDOM_GLYPHS_COUNT);
            sb.append(RANDOM_GLYPHS.charAt(index));
        }

        return sb.toString();
    }

}
