package io.tinga.b3.core.shadowing;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.javafaker.Faker;

import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.protocol.GenericB3Message;
import io.tinga.b3.protocol.topic.B3Topic;
import io.tinga.b3.protocol.topic.TestB3TopicFactory;
import io.tinga.belt.output.GadgetSink;

@ExtendWith(MockitoExtension.class)
public class SinkShadowReportedPolicyTest {

    private static final Faker faker = new Faker();

    @Mock
    GadgetSink out;
    @Mock
    EdgeDriver<GenericB3Message> edgeDriver;
    @Mock
    GenericB3Message message;
    @Spy
    B3Topic topicName = TestB3TopicFactory.instance().root().agent(faker.lorem().word());

    SinkShadowReportedPolicy<GenericB3Message> testee;

    @BeforeEach
    void setup() {
        testee = new SinkShadowReportedPolicy<>(
                GenericB3Message.class,
                out,
                edgeDriver);
    }

    @Test
    public void subscribeToDriverOnBind() {
        testee.bindTo(topicName, faker.lorem().word());
        verify(edgeDriver, times(1)).subscribe(testee);
    }

    @Test
    public void writeToSinkOnIncomingEvent() throws Exception {
        testee.handle(faker.lorem().word(), message);
        verify(out, times(1)).put(anyString());
    }

    @Test
    public void returnsTrueOnIncomingEvent() throws Exception {
        boolean result = testee.handle(faker.lorem().word(), message);
        assertTrue(result);
    }
}
