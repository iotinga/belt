package io.tinga.b3.protocol.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.tinga.b3.protocol.B3Topic;
import it.netgrid.bauer.ITopicFactory;
import it.netgrid.bauer.Topic;



@ExtendWith(MockitoExtension.class)
public class PassthroughITopicFactoryProxyTest {

    @Mock Topic<Object> topic;
    @Mock ITopicFactory topicFactory;
    @Mock B3Topic b3Topic;

    @InjectMocks PassthroughITopicFactoryProxy sut;
    

    @Test
    void testGetTopicDelegatesToFactoryCorrectlyOnRetainedTrue() {
        
        when(b3Topic.toString(true)).thenReturn("$retained/topic/true");
        when(topicFactory.getTopic("$retained/topic/true")).thenReturn(topic);

        Topic<Object> result = sut.getTopic(b3Topic, true);

        // Assert
        verify(b3Topic).toString(true);
        verify(topicFactory).getTopic("$retained/topic/true");
        assertThat(result).isSameAs(topic);
    }

    @Test
    void testGetTopicDelegatesToFactoryCorrectlyOnRetainedFalse() {
        
        when(b3Topic.toString(false)).thenReturn("topic/true");
        when(topicFactory.getTopic("topic/true")).thenReturn(topic);

        Topic<Object> result = sut.getTopic(b3Topic, false);

        // Assert
        verify(b3Topic).toString(false);
        verify(topicFactory).getTopic("topic/true");
        assertThat(result).isSameAs(topic);
    }
}
