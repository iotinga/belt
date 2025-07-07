package io.tinga.b3.helpers.messageprovider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.protocol.B3ITopicFactoryProxy;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;
import it.netgrid.bauer.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FromTopicMessageProviderTest {

    @Mock
    B3ITopicFactoryProxy topicFactoryProxy;

    @Mock
    B3Topic.Factory topicFactory;

    @Mock
    B3Topic.Valid activeB3TopicValid;

    @Mock
    B3Topic activeB3Topic;

    @Mock
    Topic<Object> topic;

    @Mock
    GenericB3Message message;

    FromTopicMessageProvider<GenericB3Message> sut;

    @BeforeEach
    void setup() {
        sut = new FromTopicMessageProvider<>(GenericB3Message.class, topicFactoryProxy, topicFactory) {
            @Override
            protected B3Topic getActiveB3Topic() {
                return activeB3Topic;
            }
        };
    }

    @Test
    void getNameShouldReturnClassName() {
        assertThat(sut.getName()).isEqualTo(FromTopicMessageProvider.class.getName());
    }

    @Test
    void getEventClassShouldReturnMessageClass() {
        assertThat(sut.getEventClass()).isEqualTo(GenericB3Message.class);
    }

    @Test
    void loadShouldWaitUntilMessageIsReceived() throws Exception {
        String requestPath = "test/path";

        when(topicFactory.parse(anyString())).thenReturn(activeB3TopicValid);
        when(activeB3TopicValid.build()).thenReturn(activeB3Topic);
        when(topicFactoryProxy.getTopic(activeB3Topic, false)).thenReturn(topic);

        doAnswer(inv -> {
            // simulate event reception after a short delay
            new Thread(() -> {
                try {
                    Thread.sleep(200);
                    sut.handle(requestPath, message);
                } catch (Exception ignored) {
                }
            }).start();
            return null;
        }).when(topic).addHandler(any());

        B3Message<?> result = sut.load(requestPath);

        assertThat(result).isEqualTo(message);
    }

    @Test
    void handleShouldSetLoadedMessageIfTopicMatches() throws Exception {
        String requestPath = "test/path";

        when(topicFactory.parse(anyString())).thenReturn(activeB3TopicValid);
        when(activeB3TopicValid.build()).thenReturn(activeB3Topic);

        boolean handled = sut.handle(requestPath, message);

        assertThat(handled).isTrue();
        assertThat(sut.getLoadedMessage()).isEqualTo(message);
    }

    @Test
    void handleShouldNotSetLoadedMessageIfActiveTopicIsNull() throws Exception {
        String requestPath = "test/path";

        sut = new FromTopicMessageProvider<>(GenericB3Message.class, topicFactoryProxy, topicFactory) {
            @Override
            protected B3Topic getActiveB3Topic() {
                return null;
            }
        };

        when(topicFactory.parse(anyString())).thenReturn(activeB3TopicValid);
        when(activeB3TopicValid.build()).thenReturn(activeB3Topic);

        boolean handled = sut.handle(requestPath, message);

        assertThat(handled).isTrue();
        assertThat(sut.getLoadedMessage()).isEqualTo(null);
    }

    @Test
    void handleShouldNotSetMessageIfTopicDoesNotMatch() throws Exception {
        String otherPath = "test/path/other";

        B3Topic.Valid otherValid = mock(B3Topic.Valid.class);
        B3Topic otherTopic = mock(B3Topic.class);

        when(topicFactory.parse(otherPath)).thenReturn(otherValid);
        when(otherValid.build()).thenReturn(otherTopic);

        boolean result = sut.handle(otherPath, message);

        assertThat(result).isTrue();
        assertThat(sut.getLoadedMessage()).isNull();
    }
}
