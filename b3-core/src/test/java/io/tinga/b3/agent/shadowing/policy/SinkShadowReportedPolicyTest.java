package io.tinga.b3.agent.shadowing.policy;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import io.tinga.b3.agent.Agent;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.TestB3TopicFactory;
import io.tinga.belt.output.GadgetSink;

@ExtendWith(MockitoExtension.class)
public class SinkShadowReportedPolicyTest {

    private static final Faker faker = new Faker();

    @Mock
    GadgetSink out;
    @Mock
    Agent.EdgeDriver<GenericB3Message> edgeDriver;
    @Mock
    GenericB3Message message;
    @Spy
    B3Topic.Base topicBase = TestB3TopicFactory.instance().agent(faker.lorem().word());

    SinkShadowReportedPolicy<GenericB3Message> sut;

    @BeforeEach
    void setup() {
        sut = new SinkShadowReportedPolicy<>(
                out,
                edgeDriver);
    }

    @Test
    public void subscribeToDriverOnBind() {
        sut.bind(topicBase, faker.lorem().word());
        verify(edgeDriver, times(1)).subscribe(sut);
        assertEquals(SinkShadowReportedPolicy.class.getSimpleName(), sut.getName());
    }

    @Test
    public void writeToSinkOnIncomingEvent() throws Exception {
        sut.handle(TestB3TopicFactory.instance().agent(faker.lorem().word()).shadow().reported().build(), message);
        verify(out, times(1)).put(anyString());
    }

    @Test
    public void returnsTrueOnIncomingEvent() throws Exception {
        boolean result = sut.handle(TestB3TopicFactory.instance().agent(faker.lorem().word()).shadow().reported().build(), message);
        assertTrue(result);
    }
}
