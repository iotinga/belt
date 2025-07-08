package io.tinga.b3.helpers.proxy;

import com.github.javafaker.Faker;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import io.tinga.b3.helpers.AgentProxy;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.TestB3TopicFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CachedAgentProxyFactoryTest {

    private static final B3Topic.Factory topicFactory = TestB3TopicFactory.instance();
    private static final Faker faker = new Faker();
    private static final String agentId = faker.lorem().word();
    private static final String roleName = faker.lorem().word();
    private static final B3Topic.Base topicBase = topicFactory.agent(agentId);
    private static final TypeLiteral<AgentProxy<GenericB3Message>> typeLiteral = new TypeLiteral<>() {};

    @Mock
    Injector injector;

    @Mock
    AgentProxy<GenericB3Message> proxyInstance;

    CachedAgentProxyFactory<GenericB3Message> sut;

    @BeforeEach
    void setUp() {
        sut = new CachedAgentProxyFactory<GenericB3Message>(injector, typeLiteral);
    }

    @Test
    void testGetProxyCreatesAndCachesInstance() {
        Key<AgentProxy<GenericB3Message>> key = Key.get(typeLiteral);
        doAnswer(invocation -> proxyInstance).when(injector).getInstance(key);

        AgentProxy<GenericB3Message> result = sut.getProxy(topicBase, roleName);

        assertThat(result).isEqualTo(proxyInstance);

        AgentProxy<GenericB3Message> result2 = sut.getProxy(topicBase, roleName);
        assertThat(result2).isSameAs(result);

        verify(injector, times(1)).getInstance(key);
        verify(proxyInstance, times(0)).bind(topicBase, roleName);
    }
}
