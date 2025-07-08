package io.tinga.b3.protocol.impl;

import static io.tinga.b3.protocol.B3Topic.GLUE;
import static io.tinga.b3.protocol.B3Topic.RETAIN_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.tinga.b3.protocol.impl.B3TopicToken.Name;

@ExtendWith(MockitoExtension.class)
public class B3TopicFactoryImplRecordTest extends StandardB3TopicFactory {
    @Test
    void testB3TopicRecordAddsRetainedOnTrue() {
        B3TopicToken token = B3TopicToken.from(Name.ROOT);
        List<B3TopicToken> tokens = List.of(token);
        B3TopicRecord sut = new B3TopicRecord(tokens);
        String result = sut.toString(true);
        assertEquals(RETAIN_PREFIX + GLUE + token.value(), result);
    }
    @Test
    void testB3TopicRecordDoesntAddRetainedOnFalse() {
        B3TopicToken token = B3TopicToken.from(Name.ROOT);
        List<B3TopicToken> tokens = List.of(token);
        B3TopicRecord sut = new B3TopicRecord(tokens);
        String result = sut.toString(false);
        assertEquals(token.value(), result);
    }
}
