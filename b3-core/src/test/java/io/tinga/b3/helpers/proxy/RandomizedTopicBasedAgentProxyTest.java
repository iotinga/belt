package io.tinga.b3.helpers.proxy;

import io.tinga.b3.agent.Agent;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.protocol.B3EventHandler;
import io.tinga.b3.protocol.B3ITopicFactoryProxy;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.TestB3TopicFactory;
import it.netgrid.bauer.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.javafaker.Faker;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RandomizedTopicBasedAgentProxyTest {

    private static final B3Topic.Factory topicFactory = TestB3TopicFactory.instance();
    private static final Faker faker = new Faker();
    private static final String agentId = faker.lorem().word();

    @Mock
    Agent.Config config;

    @Mock
    B3ITopicFactoryProxy topicFactoryProxy;

    @Mock
    Topic<GenericB3Message> desiredTopic;

    @Mock
    Topic<GenericB3Message> reportedTopic;

    @Mock
    GenericB3Message message;

    @Mock
    GenericB3Message oldMessage;

    @Mock
    B3EventHandler<GenericB3Message> handler1;

    @Mock
    B3EventHandler<GenericB3Message> handler2;

    RandomizedTopicBasedAgentProxy<GenericB3Message> sut;

    @BeforeEach
    void setup() {
        sut = new RandomizedTopicBasedAgentProxy<>(config, GenericB3Message.class, topicFactoryProxy, topicFactory);
    }

    @Test
    void testGetName() {
        when(config.agentId()).thenReturn(agentId);
        String name1 = sut.getName();
        String name2 = sut.getName();
        assertNotNull(name1);
        assertNotNull(name2);
        assertNotEquals(name1, name2);
    }
}
