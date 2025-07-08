package io.tinga.b3.agent.shadowing.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.javafaker.Faker;

import io.tinga.b3.agent.InitializationException;
import io.tinga.b3.helpers.AgentProxy;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.TestB3TopicFactory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class RetainedReportedVersionSafeExecutorTest {

    private static final B3Topic.Factory topicFactory = TestB3TopicFactory.instance();
    private static final Faker faker = new Faker();
    private static final String agentId = faker.lorem().word();
    private static final String roleName = faker.lorem().word();
    private static final B3Topic.Base topicBase = topicFactory.agent(agentId);

    @Mock
    AgentProxy<GenericB3Message> agentProxy;

    @Mock
    GenericB3Message message;

    @Mock
    AgentProxy.Factory<GenericB3Message> agentProxyFactory;

    @InjectMocks
    RetainedReportedVersionSafeExecutor<GenericB3Message> sut;

        @Test
    void shouldBindSuccessfullyWhenNotInitialized() throws Exception {
        when(agentProxyFactory.getProxy(topicBase, roleName)).thenReturn(agentProxy);

        sut.bind(topicBase, roleName);

        verify(agentProxyFactory).getProxy(topicBase, roleName);
        verify(agentProxy).subscribe(sut);
    }

    @Test
    void shouldNotBindIfAlreadyInitialized() throws Exception {
        when(agentProxyFactory.getProxy(topicBase, roleName)).thenReturn(agentProxy);
        sut.bind(topicBase, roleName);
        sut.bind(topicBase, roleName);

        verify(agentProxyFactory, times(1)).getProxy(topicBase, roleName);
        verify(agentProxy, times(1)).subscribe(sut);
    }

    @Test
    void shouldThrowInitializationExceptionIfBindingFails() {
        when(agentProxyFactory.getProxy(topicBase, roleName))
                .thenThrow(new RuntimeException("failure"));

        assertThatThrownBy(() -> sut.bind(topicBase, roleName))
                .isInstanceOf(InitializationException.class)
                .hasMessageContaining("failure");
    }

    @Test
    void shouldHandleAndInitializeIfNotYetInitialized() throws Exception {
        when(agentProxyFactory.getProxy(topicBase, roleName)).thenReturn(agentProxy);
        when(message.getVersion()).thenReturn(42);

        sut.bind(topicBase, roleName);
        boolean result = sut.handle(topicBase.shadow().reported().build(), message);

        assertThat(result).isTrue();
        verify(agentProxy).unsubscribe(sut);
    }

    @Test
    void shouldHandleAndNotReinitializeIfAlreadyInitialized() throws Exception {
        when(agentProxyFactory.getProxy(topicBase, roleName)).thenReturn(agentProxy);
        when(message.getVersion()).thenReturn(7);

        sut.bind(topicBase, roleName);
        sut.handle(topicBase.shadow().reported().build(), message);
        boolean result = sut.handle(topicBase.shadow().reported().build(), message);

        assertThat(result).isTrue();
        verify(agentProxy, times(1)).unsubscribe(sut);
    }

    @Test
    void getNameShouldReturnClassName() {
        assertThat(sut.getName()).isEqualTo("RetainedReportedVersionSafeExecutor");
    }
}
