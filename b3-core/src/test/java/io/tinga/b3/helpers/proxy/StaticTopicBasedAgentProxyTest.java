package io.tinga.b3.helpers.proxy;

import io.tinga.b3.agent.Agent;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.protocol.B3EventHandler;
import io.tinga.b3.protocol.B3ITopicFactoryProxy;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.TestB3TopicFactory;
import it.netgrid.bauer.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.javafaker.Faker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StaticTopicBasedAgentProxyTest {

    private static final B3Topic.Factory topicFactory = TestB3TopicFactory.instance();
    private static final Faker faker = new Faker();
    private static final String agentId = faker.lorem().word();
    private static final String roleName = faker.lorem().word();
    private static final B3Topic.Base topicBase = topicFactory.agent(agentId);

    @Mock
    Agent.Config config;

    @Mock
    B3ITopicFactoryProxy topicFactoryProxy;

    @Mock
    Topic<GenericB3Message> desiredTopic;

    @Mock
    Topic<GenericB3Message> reportedTopic;

    @Mock
    GenericB3Message message;

    @Mock
    GenericB3Message oldMessage;

    @Mock
    B3EventHandler<GenericB3Message> handler1;

    @Mock
    B3EventHandler<GenericB3Message> handler2;

    StaticTopicBasedAgentProxy<GenericB3Message> sut;

    @BeforeEach
    void setup() {
        // when(config.agentId()).thenReturn(agentId);
        sut = new StaticTopicBasedAgentProxy<>(config, GenericB3Message.class, topicFactoryProxy, topicFactory);
    }

    @Test
    void testGetName() {
        String name = sut.getName();
        assertThat(name).isEqualTo(String.format("%s-%s", config.agentId(), StaticTopicBasedAgentProxy.class.getSimpleName()));
    }

    @Test
    void testBindInitializesOnlyOnce() {
        B3Topic reported = topicFactory.agent(agentId).shadow().reported().build();
        B3Topic desired = topicFactory.agent(agentId).shadow().desired(roleName).build();

        doAnswer(invocation -> desiredTopic).when(topicFactoryProxy).getTopic(eq(desired), anyBoolean());
        doAnswer(invocation -> reportedTopic).when(topicFactoryProxy).getTopic(eq(reported), anyBoolean());

        sut.bind(topicBase, roleName);
        sut.bind(topicBase, roleName);
        verify(topicFactoryProxy, times(1)).getTopic(eq(desired), anyBoolean());
        verify(topicFactoryProxy, times(1)).getTopic(eq(reported), anyBoolean());
        verify(reportedTopic, times(1)).addHandler(sut);
    }

    @Test
    void testBoundGetters() {
        B3Topic reported = topicFactory.agent(agentId).shadow().reported().build();
        B3Topic desired = topicFactory.agent(agentId).shadow().desired(roleName).build();

        doAnswer(invocation -> desiredTopic).when(topicFactoryProxy).getTopic(eq(desired), anyBoolean());
        doAnswer(invocation -> reportedTopic).when(topicFactoryProxy).getTopic(eq(reported), anyBoolean());

        sut.bind(topicBase, roleName);
        assertEquals(topicBase, sut.getBoundTopicBase());
        assertEquals(roleName, sut.getBoundRoleName());
    }

    @Test
    void testWriteSetsVersionAndPosts() {
        when(oldMessage.getVersion()).thenReturn(1);
        B3Topic reported = topicFactory.agent(agentId).shadow().reported().build();
        B3Topic desired = topicFactory.agent(agentId).shadow().desired(roleName).build();

        doAnswer(invocation -> desiredTopic).when(topicFactoryProxy).getTopic(eq(desired), anyBoolean());
        doAnswer(invocation -> reportedTopic).when(topicFactoryProxy).getTopic(eq(reported), anyBoolean());
        sut.lastShadowReported = oldMessage;
        sut.bind(topicBase, roleName);
        sut.write(message);
        verify(message).setVersion(1);
        verify(desiredTopic).post(message);
    }

    @Test
    void testWriteSkipsIfNotBound() {
        sut.write(message);
        verifyNoInteractions(message);
    }

    @Test
    void testSafeUpdateLastShadowReportedAcceptsNewerVersion() throws Exception {
        B3Topic topic = topicBase.shadow().reported().build();
        when(message.getVersion()).thenReturn(2);

        when(oldMessage.getVersion()).thenReturn(1);
        sut.lastShadowReported = oldMessage;

        sut.subscribe(handler1);
        sut.subscribe(handler2);

        sut.handle(topicBase.shadow().reported().build().toString(), message);

        verify(handler1).handle(topic, message);
        verify(handler2).handle(topic, message);
    }

    // @Test
    // void testSafeUpdateLastShadowReportedAcceptsNewerVersion() throws Exception {
    //     when(message.getVersion()).thenReturn(2);

    //     when(oldMessage.getVersion()).thenReturn(1);
    //     sut.lastShadowReported = oldMessage;

    //     sut.subscribe(handler1);
    //     sut.subscribe(handler2);

    //     sut.handle(topicBase.shadow().reported().build(), message);

    //     verify(handler1).handle(topicBase.shadow().reported().build(), message);
    //     verify(handler2).handle(topicBase.shadow().reported().build(), message);
    // }

    @Test
    void testSafeUpdateLastShadowReportedRejectsOlderVersion() throws Exception {
        when(message.getVersion()).thenReturn(1);

        when(oldMessage.getVersion()).thenReturn(2);
        sut.lastShadowReported = oldMessage;

        sut.subscribe(handler1);

        sut.handle(topicBase.shadow().reported().build(), message);

        verify(handler1, never()).handle(any(), any());
    }

    @Test
    void testSubscribeAndUnsubscribe() {
        sut.subscribe(handler1);
        sut.subscribe(handler2);
        sut.unsubscribe(handler1);

        assertThat(sut.subscribers).containsOnly(handler2);
    }
}
