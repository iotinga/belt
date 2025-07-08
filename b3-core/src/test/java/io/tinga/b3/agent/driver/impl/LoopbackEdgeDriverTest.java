package io.tinga.b3.agent.driver.impl;

import io.tinga.b3.agent.driver.ConnectionState;
import io.tinga.b3.agent.driver.EdgeDriverException;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.protocol.B3EventHandler;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.TestB3TopicFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.javafaker.Faker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoopbackEdgeDriverTest {

    private static final B3Topic.Factory topicFactory = TestB3TopicFactory.instance();
    private static final Faker faker = new Faker();
    private static final String agentId = faker.lorem().word();

    @Spy
    B3Topic.Base topicBase = topicFactory.agent(agentId);
    
    B3Topic topic = topicBase.shadow().reported().build();

    @Mock
    private GenericB3Message message;

    @Mock
    private B3EventHandler<GenericB3Message> subscriber;

    @InjectMocks
    private LoopbackEdgeDriver<GenericB3Message> sut;

    @Test
    void shouldDoNothingOnConnectAndDisconnect() {
        sut.connect();
        sut.disconnect();
        // no exception thrown is sufficient
    }

    @Test
    void shouldAlwaysBeConnected() {
        assertThat(sut.getConnectionState()).isEqualTo(ConnectionState.CONNECTED);
    }

    @Test
    void shouldThrowIfMessageIsNull() {
        assertThatThrownBy(() -> sut.write(null))
                .isInstanceOf(EdgeDriverException.class)
                .hasMessageContaining("desiredMessage is null");
    }

    @Test
    void shouldDeliverMessageToSubscriber() throws Exception {
        sut.subscribe(subscriber);

        sut.write(message);

        verify(subscriber).handle(topic, message);
    }

    @Test
    void shouldThrowIfSubscriberFails() throws Exception {
        sut.subscribe(subscriber);
        doThrow(new RuntimeException("handler failed")).when(subscriber).handle(topic, message);
        when(subscriber.getName()).thenReturn("TestSubscriber");

        assertThatThrownBy(() -> sut.write(message))
                .isInstanceOf(EdgeDriverException.class)
                .hasMessageContaining("Unexpected error occurred sending message to TestSubscriber");
    }

    @Test
    void shouldAddAndRemoveSubscriber() throws Exception {
        sut.subscribe(subscriber);
        sut.write(message); // should deliver

        verify(subscriber, times(1)).handle(topic, message);

        sut.unsubscribe(subscriber);
        sut.write(message); // should not deliver

        verifyNoMoreInteractions(subscriber);
    }
}
