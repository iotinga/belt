package io.tinga.b3.agent.shadowing.policy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.javafaker.Faker;

import io.tinga.b3.agent.Agent;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor.CriticalSection;
import io.tinga.b3.helpers.AgentProxy;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.TestB3TopicFactory;
import io.tinga.b3.protocol.B3ITopicFactoryProxy;
import it.netgrid.bauer.Topic;

@ExtendWith(MockitoExtension.class)
public class EdgeFirstShadowReportedPolicyTest {

    private static final Faker faker = new Faker();

    @Mock
    GenericB3Message firstMessage;
    @Mock
    GenericB3Message secondMessage;
    @Mock
    GenericB3Message thirdMessage;
    @Mock
    VersionSafeExecutor executor;
    @Mock
    Agent.EdgeDriver<GenericB3Message> driver;
    @Mock
    B3ITopicFactoryProxy factoryProxy;
    @Mock
    Topic<GenericB3Message> topic;
    @Mock
    AgentProxy.Factory<GenericB3Message> agentProxyFactory;
    @Mock
    AgentProxy<GenericB3Message> agentProxy;

    @Spy
    B3Topic.Base topicBase = TestB3TopicFactory.instance().agent(faker.lorem().word());

    EdgeFirstShadowReportedPolicy<GenericB3Message> sut;

    @BeforeEach
    void setup() {
        sut = new EdgeFirstShadowReportedPolicy<>(
                executor,
                driver,
                agentProxyFactory,
                factoryProxy);
    }

    @Test
    public void checkBasicGetters() {
        doAnswer(invocation -> agentProxy).when(agentProxyFactory).getProxy(eq(topicBase), anyString());
        // doAnswer(invocation -> topic).when(factoryProxy).getTopic(any(B3Topic.class),
        // eq(true));
        sut.bind(topicBase, faker.lorem().word());
        assertEquals(sut.getClass().getSimpleName(), sut.getName());
        assertEquals(null, sut.getTopic());
    }

    @Test
    public void initProxyOnBind() {
        doAnswer(invocation -> agentProxy).when(agentProxyFactory).getProxy(eq(topicBase), anyString());
        // doAnswer(invocation -> topic).when(factoryProxy).getTopic(any(B3Topic.class),
        // eq(true));
        sut.bind(topicBase, faker.lorem().word());
        verify(agentProxyFactory, times(1)).getProxy(eq(topicBase), anyString());
        verify(agentProxy, times(1)).subscribe(sut);
    }

    @Test
    public void subscribesToDriverAfterInitialization() throws Exception {
        B3Topic reportedTopic = topicBase.shadow().reported().build();
        doAnswer(invocation -> agentProxy).when(agentProxyFactory).getProxy(eq(topicBase), anyString());
        sut.bind(topicBase, faker.lorem().word());
        sut.handle(reportedTopic, firstMessage);
        verify(agentProxy, times(1)).unsubscribe(sut);
        verify(driver, times(1)).subscribe(sut);
        verify(factoryProxy, times(1)).getTopic(reportedTopic, true);
    }

    @Test
    public void doesntPostAMessageEqualToThePreviouslySent() throws Exception {
        final int currentVersion = faker.random().nextInt(1, 1000);
        final int nextVersion = currentVersion + 1;

        B3Topic reportedTopic = topicBase.shadow().reported().build();
        B3Topic desiredTopic = topicBase.shadow().reported().build();

        Function<Boolean, Integer> version = (Boolean next) -> {
            return next ? nextVersion : currentVersion;
        };

        doAnswer(invocation -> {
            CriticalSection section = invocation.getArgument(0);
            section.apply(version);
            return null;
        }).when(executor).safeExecute(any());

        doAnswer(invocation -> agentProxy).when(agentProxyFactory).getProxy(eq(topicBase), anyString());
        doAnswer(invocation -> topic).when(factoryProxy).getTopic(any(B3Topic.class), eq(true));
        doAnswer(invocation -> Integer.valueOf(currentVersion)).when(secondMessage).getVersion();
        sut.bind(topicBase, faker.lorem().word());

        // the firstone is the initialization message
        sut.handle(reportedTopic, firstMessage);

        sut.handle(desiredTopic, secondMessage);
        boolean result = sut.handle(reportedTopic, secondMessage);
        verify(topic, times(1)).post(secondMessage);
        assertTrue(result);
        assertEquals(secondMessage, sut.getLastSentMessage());
    }

    @Test
    public void postsAMessageNotEqualToThePreviouslySent() throws Exception {
        final int currentVersion = faker.random().nextInt(1, 1000);
        final int nextVersion = currentVersion + 1;

        B3Topic reportedTopic = topicBase.shadow().reported().build();
        B3Topic desiredTopic = topicBase.shadow().reported().build();

        Function<Boolean, Integer> version = (Boolean next) -> {
            return next ? nextVersion : currentVersion;
        };

        doAnswer(invocation -> {
            CriticalSection section = invocation.getArgument(0);
            section.apply(version);
            return null;
        }).when(executor).safeExecute(any());

        doAnswer(invocation -> agentProxy).when(agentProxyFactory).getProxy(eq(topicBase), anyString());
        doAnswer(invocation -> topic).when(factoryProxy).getTopic(any(B3Topic.class), eq(true));
        doAnswer(invocation -> Integer.valueOf(currentVersion)).when(secondMessage).getVersion();
        sut.bind(topicBase, faker.lorem().word());

        // the firstone is the initialization message
        sut.handle(reportedTopic, firstMessage);

        sut.handle(desiredTopic, secondMessage);
        boolean result = sut.handle(reportedTopic, thirdMessage);
        verify(topic, times(1)).post(secondMessage);
        verify(topic, times(1)).post(thirdMessage);
        assertTrue(result);
        assertEquals(thirdMessage, sut.getLastSentMessage());
    }

    @Test
    public void incrementsVersionOnOutcomingMessage() throws Exception {
        final int currentVersion = faker.random().nextInt(1, 1000);
        final int nextVersion = currentVersion + 1;

        B3Topic reportedTopic = topicBase.shadow().reported().build();
        B3Topic desiredTopic = topicBase.shadow().reported().build();

        Function<Boolean, Integer> version = (Boolean next) -> {
            return next ? nextVersion : currentVersion;
        };

        doAnswer(invocation -> {
            CriticalSection section = invocation.getArgument(0);
            section.apply(version);
            return null;
        }).when(executor).safeExecute(any());

        doAnswer(invocation -> agentProxy).when(agentProxyFactory).getProxy(eq(topicBase), anyString());
        doAnswer(invocation -> topic).when(factoryProxy).getTopic(any(B3Topic.class), eq(true));
        doAnswer(invocation -> Integer.valueOf(currentVersion)).when(secondMessage).getVersion();
        sut.bind(topicBase, faker.lorem().word());

        // the firstone is the initialization message
        sut.handle(reportedTopic, firstMessage);

        sut.handle(desiredTopic, secondMessage);
        boolean result = sut.handle(reportedTopic, secondMessage);
        verify(secondMessage, times(1)).setVersion(nextVersion);
        assertTrue(result);
    }
}
