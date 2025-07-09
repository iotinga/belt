package io.tinga.b3.agent.security.impl;

import io.tinga.b3.agent.B3InvalidOperationException;
import io.tinga.b3.agent.security.Operation;
import io.tinga.b3.helpers.GenericB3Message;
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
public class StandardOperationFactoryTest {

    private static final Faker faker = new Faker();
    private static final String agentId = faker.lorem().word();
    private static final String roleName = faker.lorem().word();

    @Spy
    private B3Topic.Factory topicFactory = TestB3TopicFactory.instance();
    @Spy
    private B3Topic.Base topicBase = topicFactory.agent(agentId);
    @Spy
    private B3Topic.Valid topic = topicBase.shadow().desired(roleName);

    @Mock
    private GenericB3Message message;

    @InjectMocks
    private StandardOperationFactory sut;

    @Test
    void shouldBuildOperationFromTopicPath() throws Exception {
        when(topicFactory.parse(topic.build().toString())).thenReturn(topic);
        Operation<GenericB3Message> operation = sut.buildFrom(topic.build().toString(), message);

        assertThat(operation).isNotNull();
        assertThat(operation.sourceTopic()).isEqualTo(topic.build());
        assertThat(operation.message()).isEqualTo(message);
    }

    @Test
    void shouldBuildOperationFromB3Topic() throws Exception {
        Operation<GenericB3Message> operation = sut.buildFrom(topic.build(), message);

        assertThat(operation).isNotNull();
        assertThat(operation.sourceTopic()).isEqualTo(topic.build());
        assertThat(operation.message()).isEqualTo(message);
    }

    @Test
    void shouldThrowInvalidOperationExceptionWhenTopicIsNull() {
        assertThatThrownBy(() -> sut.buildFrom((B3Topic) null, message))
                .isInstanceOf(B3InvalidOperationException.class);
    }

    @Test
    void shouldThrowInvalidOperationExceptionWhenMessageIsNull() {
        assertThatThrownBy(() -> sut.buildFrom(topic.build(), null))
                .isInstanceOf(B3InvalidOperationException.class);
    }
}
