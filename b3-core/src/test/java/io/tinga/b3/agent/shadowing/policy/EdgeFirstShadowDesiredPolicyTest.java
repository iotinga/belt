package io.tinga.b3.agent.shadowing.policy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.javafaker.Faker;

import io.tinga.b3.agent.Agent;
import io.tinga.b3.agent.InvalidOperationException;
import io.tinga.b3.agent.security.Operation;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor.CriticalSection;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.B3TopicValidationException;
import io.tinga.b3.protocol.TestB3TopicFactory;
import io.tinga.b3.protocol.B3ITopicFactoryProxy;
import it.netgrid.bauer.Topic;

@ExtendWith(MockitoExtension.class)
public class EdgeFirstShadowDesiredPolicyTest {

    private static final Faker faker = new Faker();

    @Mock
    GenericB3Message message;
    @Mock
    VersionSafeExecutor executor;
    @Mock
    Agent.EdgeDriver<GenericB3Message> driver;
    @Mock
    B3ITopicFactoryProxy factoryProxy;
    @Mock
    Topic<GenericB3Message> topic;
    @Mock
    Operation.Factory operationFactory;
    @Mock
    Operation.GrantsChecker<GenericB3Message> checker;
    @Mock
    B3Topic.Factory topicFactory;
    @Spy
    B3Topic.Base topicBase = TestB3TopicFactory.instance().agent(faker.lorem().word());

    EdgeFirstShadowDesiredPolicy<GenericB3Message> sut;

    @BeforeEach
    void setup() {
        sut = new EdgeFirstShadowDesiredPolicy<>(
                GenericB3Message.class,
                executor,
                driver,
                factoryProxy, operationFactory, topicFactory, checker);
    }

    @Test
    public void checkBasicGetters() {
        assertEquals(sut.getClass().getName(), sut.getName());
        assertEquals(GenericB3Message.class, sut.getEventClass());
    }

    @Test
    public void addTopicHandlerOnBind() {
        doAnswer(invocation -> topic).when(factoryProxy).getTopic(any(B3Topic.class), eq(false));
        sut.bind(topicBase, faker.lorem().word());
        verify(factoryProxy, times(1)).getTopic(any(B3Topic.class), eq(false));
        verify(topic, times(1)).addHandler(sut);
    }

    @Test
    public void detectAConflictWhenDesiredVersionDifferentThanCurrent() {
        int currentVersion = faker.random().nextInt(1, 1000);
        int messageVersion = faker.random().nextInt(1001, 2000);
        doAnswer(invocation -> Integer.valueOf(messageVersion)).when(message).getVersion();
        boolean result = sut.hasConflicts(flag -> currentVersion, message);
        assertTrue(result);
    }

    @Test
    public void doesntDetectAConflictWhenDesiredVersionEqualToCurrent() {
        int currentVersion = faker.random().nextInt(1, 1000);
        doAnswer(invocation -> Integer.valueOf(currentVersion)).when(message).getVersion();
        boolean result = sut.hasConflicts(flag -> currentVersion, message);
        assertFalse(result);
    }

    @Test
    public void doesntDetectAConflictWhenDesiredVersionIsWildcard() {
        int currentVersion = faker.random().nextInt(1, 1000);
        doAnswer(invocation -> Integer.valueOf(Agent.VERSION_WILDCARD)).when(message).getVersion();
        boolean result = sut.hasConflicts(flag -> currentVersion, message);
        assertFalse(result);
    }

    @Test
    public void writesAConflictFreeMessage() throws B3TopicValidationException, Exception {
        int currentVersion = faker.random().nextInt(1, 1000);

        doAnswer(invocation -> true).when(checker).isAllowed(any());
        doAnswer(invocation -> {
            CriticalSection section = invocation.getArgument(0);
            section.apply(flag -> currentVersion);
            return null;
        }).when(executor).safeExecute(any());

        doAnswer(invocation -> Integer.valueOf(currentVersion)).when(message).getVersion();

        boolean result = sut.handle(topicBase.shadow().desired(faker.lorem().word()).build(), message);
        verify(driver, times(1)).write(message);
        assertTrue(result);
    }

    @Test
    public void doesntWriteANotAllowedOperation() throws B3TopicValidationException, Exception {
        int currentVersion = faker.random().nextInt(1, 1000);

        doAnswer(invocation -> false).when(checker).isAllowed(any());
        doAnswer(invocation -> {
            CriticalSection section = invocation.getArgument(0);
            section.apply(flag -> currentVersion);
            return null;
        }).when(executor).safeExecute(any());

        doAnswer(invocation -> Integer.valueOf(currentVersion)).when(message).getVersion();

        boolean result = sut.handle(topicBase.shadow().desired(faker.lorem().word()).build(), message);
        verify(driver, times(0)).write(message);
        assertTrue(result);
    }

    @Test
    public void doesntWriteAConflictingMessage() throws B3TopicValidationException, Exception {
        int currentVersion = faker.random().nextInt(1, 1000);
        int messageVersion = faker.random().nextInt(1001, 2000);

        doAnswer(invocation -> {
            CriticalSection section = invocation.getArgument(0);
            section.apply(flag -> currentVersion);
            return null;
        }).when(executor).safeExecute(any());

        doAnswer(invocation -> Integer.valueOf(messageVersion)).when(message).getVersion();
        boolean result = sut.handle(topicBase.shadow().desired(faker.lorem().word()).build(), message);
        verify(driver, times(0)).write(message);
        assertTrue(result);
    }

    @Test
    public void doesNothingOnInvalidOperation() throws B3TopicValidationException, Exception {
        int currentVersion = faker.random().nextInt(1, 1000);
        B3Topic testTopic = topicBase.shadow().desired(faker.lorem().word()).build();

        doThrow(InvalidOperationException.class).when(operationFactory).buildFrom(eq(testTopic), any());

        doAnswer(invocation -> {
            CriticalSection section = invocation.getArgument(0);
            section.apply(flag -> currentVersion);
            return null;
        }).when(executor).safeExecute(any());

        doAnswer(invocation -> Integer.valueOf(currentVersion)).when(message).getVersion();

        boolean result = sut.handle(testTopic, message);
        verify(driver, times(0)).write(message);
        assertTrue(result);
    }
}
