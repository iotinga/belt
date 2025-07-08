package io.tinga.b3.helpers.proxy;

import io.tinga.b3.helpers.B3MessageProvider;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.protocol.B3EventHandler;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.TestB3TopicFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.javafaker.Faker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReadOnlyMessageProviderAgentProxyTest {

    private static final B3Topic.Factory topicFactory = TestB3TopicFactory.instance();
    private static final Faker faker = new Faker();
    private static final String agentId = faker.lorem().word();
    private static final String roleName = faker.lorem().word();
    private static final B3Topic.Base topicBase = topicFactory.agent(agentId);

    @Mock
    B3MessageProvider<GenericB3Message> provider;

    @Mock
    B3EventHandler<GenericB3Message> handler;

    @Mock
    GenericB3Message message;


    ReadOnlyMessageProviderAgentProxy<GenericB3Message> sut;

    @BeforeEach
    void setUp() {
        sut = new ReadOnlyMessageProviderAgentProxy<>(provider);
    }

    @Test
    void testSubscribeAndUnsubscribe() {
        sut.subscribe(handler);
        sut.unsubscribe(handler);
    }

    @Test
    void testBindLoadsAndNotifiesSubscribers() throws Exception {
        B3Topic topic = topicBase.shadow().reported().build();
        when(provider.load(topic.toString())).thenReturn(message);

        sut.subscribe(handler);

        sut.bind(topicBase, roleName);

        assertThat(sut.getBoundTopicBase()).isEqualTo(topicBase);
        assertThat(sut.getBoundRoleName()).isEqualTo(roleName);
        verify(provider, times(1)).load(topic.toString());
        verify(handler, times(1)).handle(topic, message);
    }

    @Test
    void testWriteThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> sut.write(message));
    }
}
