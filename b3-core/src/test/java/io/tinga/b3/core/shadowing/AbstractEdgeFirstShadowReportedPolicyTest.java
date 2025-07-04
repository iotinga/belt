package io.tinga.b3.core.shadowing;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import io.tinga.b3.agent.shadowing.impl.EdgeFirstShadowReportedPolicy;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.B3ITopicFactoryProxy;
import io.tinga.b3.protocol.impl.GenericB3Message;
import io.tinga.b3.protocol.topic.TestB3TopicFactory;
import it.netgrid.bauer.Topic;

@ExtendWith(MockitoExtension.class)
public class AbstractEdgeFirstShadowReportedPolicyTest {

    private static final Faker faker = new Faker();

    @Mock
    GenericB3Message firstMessage;
    @Mock
    GenericB3Message secondMessage;
    @Mock
    VersionSafeExecutor executor;
    @Mock
    Agent.EdgeDriver<GenericB3Message> driver;
    @Mock
    B3ITopicFactoryProxy factoryProxy;
    @Mock
    Topic<GenericB3Message> topic;

    @Spy
    B3Topic.Root topicRoot = TestB3TopicFactory.instance().agent(faker.lorem().word());

    EdgeFirstShadowReportedPolicy<GenericB3Message> testee;

    @BeforeEach
    void setup() {
        testee = new EdgeFirstShadowReportedPolicy<>(
                executor,
                driver,
                factoryProxy);
    }

    @Test
    public void initTopicOnBind() {
        doAnswer(invocation -> topic).when(factoryProxy).getTopic(any(B3Topic.class), eq(true));
        testee.bindTo(topicRoot, faker.lorem().word());
        verify(factoryProxy, times(1)).getTopic(any(B3Topic.class), eq(true));
    }

    @Test
    public void subscribesToDriverOnBind() {
        doAnswer(invocation -> topic).when(factoryProxy).getTopic(any(B3Topic.class), eq(true));
        testee.bindTo(topicRoot, faker.lorem().word());
        verify(driver, times(1)).subscribe(testee);
    }

    @Test
    public void doesntPostAMessageEqualToThePreviouslySent() throws Exception {
        final int currentVersion = faker.random().nextInt(1, 1000);
        final int nextVersion = currentVersion + 1;
        Function<Boolean, Integer> version = (Boolean next) -> {
            return next ? nextVersion : currentVersion;
        };

        doAnswer(invocation -> {
            CriticalSection section = invocation.getArgument(0);
            section.apply(version);
            return null;
        }).when(executor).safeExecute(any());

        doAnswer(invocation -> topic).when(factoryProxy).getTopic(any(B3Topic.class), eq(true));
        doAnswer(invocation -> Integer.valueOf(currentVersion)).when(firstMessage).getVersion();
        testee.bindTo(topicRoot, faker.lorem().word());
        testee.handle(topicRoot.shadow().desired(faker.lorem().word()).build(), firstMessage);
        boolean result = testee.handle(topicRoot.shadow().desired(faker.lorem().word()).build(), firstMessage);
        verify(topic, times(1)).post(firstMessage);
        assertTrue(result);
    }

    @Test
    public void postsAMessageNotEqualToThePreviouslySent() throws Exception {
        final int currentVersion = faker.random().nextInt(1, 1000);
        final int nextVersion = currentVersion + 1;
        Function<Boolean, Integer> version = (Boolean next) -> {
            return next ? nextVersion : currentVersion;
        };

        doAnswer(invocation -> {
            CriticalSection section = invocation.getArgument(0);
            section.apply(version);
            return null;
        }).when(executor).safeExecute(any());

        doAnswer(invocation -> topic).when(factoryProxy).getTopic(any(B3Topic.class), eq(true));
        doAnswer(invocation -> Integer.valueOf(currentVersion)).when(firstMessage).getVersion();
        doAnswer(invocation -> Integer.valueOf(currentVersion)).when(secondMessage).getVersion();
        testee.bindTo(topicRoot, faker.lorem().word());
        testee.handle(topicRoot.shadow().desired(faker.lorem().word()).build(), firstMessage);
        boolean result = testee.handle(topicRoot.shadow().desired(faker.lorem().word()).build(), secondMessage);
        verify(topic, times(1)).post(secondMessage);
        assertTrue(result);
    }

    @Test
    public void incrementsVersionOnOutcomingMessage() throws Exception {
        final int currentVersion = faker.random().nextInt(1, 1000);
        final int nextVersion = currentVersion + 1;
        Function<Boolean, Integer> version = (Boolean next) -> {
            return next ? nextVersion : currentVersion;
        };

        doAnswer(invocation -> {
            CriticalSection section = invocation.getArgument(0);
            section.apply(version);
            return null;
        }).when(executor).safeExecute(any());

        doAnswer(invocation -> topic).when(factoryProxy).getTopic(any(B3Topic.class), eq(true));
        doAnswer(invocation -> Integer.valueOf(currentVersion)).when(firstMessage).getVersion();
        testee.bindTo(topicRoot, faker.lorem().word());
        boolean result = testee.handle(topicRoot.shadow().desired(faker.lorem().word()).build(), firstMessage);
        verify(firstMessage, times(1)).setVersion(nextVersion);
        assertTrue(result);
    }
}
