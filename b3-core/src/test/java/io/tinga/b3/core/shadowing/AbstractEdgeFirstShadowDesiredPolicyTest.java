package io.tinga.b3.core.shadowing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.javafaker.Faker;

import io.tinga.b3.core.Agent;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.core.shadowing.VersionSafeExecutor.CriticalSection;
import io.tinga.b3.core.shadowing.impl.EdgeFirstShadowDesiredPolicy;
import io.tinga.b3.protocol.GenericB3Message;
import io.tinga.b3.protocol.TopicNameValidationException;
import io.tinga.b3.protocol.topic.B3TopicFactory;
import io.tinga.b3.protocol.topic.B3TopicRoot;
import io.tinga.b3.protocol.topic.TestB3TopicFactory;
import it.netgrid.bauer.Topic;

@ExtendWith(MockitoExtension.class)
public class AbstractEdgeFirstShadowDesiredPolicyTest {

    private static final Faker faker = new Faker();

    @Mock
    GenericB3Message message;
    @Mock
    VersionSafeExecutor executor;
    @Mock
    Agent.EdgeDriver<GenericB3Message> driver;
    @Mock
    ITopicFactoryProxy factoryProxy;
    @Mock
    Topic<GenericB3Message> topic;
    @Mock
    Operation.Factory operationFactory;
    @Mock
    Operation.GrantsChecker<GenericB3Message> checker;
    @Mock
    B3TopicFactory topicFactory;
    @Spy
    B3TopicRoot topicRoot = TestB3TopicFactory.instance().agent(faker.lorem().word());

    EdgeFirstShadowDesiredPolicy<GenericB3Message> testee;

    @BeforeEach
    void setup() {
        testee = new EdgeFirstShadowDesiredPolicy<>(
                GenericB3Message.class,
                executor,
                driver,
                factoryProxy, operationFactory, topicFactory, checker);
    }

    @Test
    public void addTopicHandlerOnBind() {
        doAnswer(invocation -> topic).when(factoryProxy).getTopic(any(B3TopicRoot.Name.class), eq(false));
        testee.bindTo(topicRoot, faker.lorem().word());
        verify(factoryProxy, times(1)).getTopic(any(B3TopicRoot.Name.class), eq(false));
        verify(topic, times(1)).addHandler(testee);
    }

    @Test
    public void detectAConflictWhenDesiredVersionDifferentThanCurrent() {
        int currentVersion = faker.random().nextInt(1, 1000);
        int messageVersion = faker.random().nextInt(1001, 2000);
        doAnswer(invocation -> Integer.valueOf(messageVersion)).when(message).getVersion();
        boolean result = testee.hasConflicts(flag -> currentVersion, message);
        assertTrue(result);
    }

    @Test
    public void doesntDetectAConflictWhenDesiredVersionEqualToCurrent() {
        int currentVersion = faker.random().nextInt(1, 1000);
        doAnswer(invocation -> Integer.valueOf(currentVersion)).when(message).getVersion();
        boolean result = testee.hasConflicts(flag -> currentVersion, message);
        assertFalse(result);
    }

    @Test
    public void doesntDetectAConflictWhenDesiredVersionIsWildcard() {
        int currentVersion = faker.random().nextInt(1, 1000);
        doAnswer(invocation -> Integer.valueOf(Agent.VERSION_WILDCARD)).when(message).getVersion();
        boolean result = testee.hasConflicts(flag -> currentVersion, message);
        assertFalse(result);
    }

    @Test
    public void writesAConflictFreeMessage() throws TopicNameValidationException, Exception {
        int currentVersion = faker.random().nextInt(1, 1000);

        doAnswer(invocation -> true).when(checker).isAllowed(any());
        doAnswer(invocation -> {
            CriticalSection section = invocation.getArgument(0);
            section.apply(flag -> currentVersion);
            return null;
        }).when(executor).safeExecute(any());

        doAnswer(invocation -> Integer.valueOf(currentVersion)).when(message).getVersion();

        boolean result = testee.handle(topicRoot.shadow().desired(faker.lorem().word()).build(), message);
        verify(driver, times(1)).write(message);
        assertTrue(result);
    }

    @Test
    public void doesntWriteAConflictingMessage() throws TopicNameValidationException, Exception {
        int currentVersion = faker.random().nextInt(1, 1000);
        int messageVersion = faker.random().nextInt(1001, 2000);

        doAnswer(invocation -> {
            CriticalSection section = invocation.getArgument(0);
            section.apply(flag -> currentVersion);
            return null;
        }).when(executor).safeExecute(any());

        doAnswer(invocation -> Integer.valueOf(messageVersion)).when(message).getVersion();
        boolean result = testee.handle(topicRoot.shadow().desired(faker.lorem().word()).build(), message);
        verify(driver, times(0)).write(message);
        assertTrue(result);
    }
}
