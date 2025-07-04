package io.tinga.b3.protocol.topic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.TopicNameValidationException;
import io.tinga.b3.protocol.impl.B3TopicFactoryImpl;


@ExtendWith(MockitoExtension.class)
public class B3TopicFactoryImplParseTest {

    @InjectMocks B3TopicFactoryImpl sut;

    @Test
    void customRoot() {
        sut = new B3TopicFactoryImpl("test");
        B3Topic.Root agent = sut.agent("item1");
        assertEquals("test/agent/item1/shadow/reported", agent.shadow().reported().build().toString());
    }

    @Test
    void removesLeadingGlueFromCustomRoot() {
        sut = new B3TopicFactoryImpl("test/");
        B3Topic.Root agent = sut.agent("item1");
        assertEquals("test/agent/item1/shadow/reported", agent.shadow().reported().build().toString());
    }
    @Test
    void defaultsOnNullCustomRoot() {
        sut = new B3TopicFactoryImpl(null);
        B3Topic.Root agent = sut.agent("item1");
        assertEquals("b3/agent/item1/shadow/reported", agent.shadow().reported().build().toString());
    }


    @Test
    void buildValidAgentName() {
        B3Topic.Root agent = sut.agent("item1");
        assertEquals("b3/agent/item1/shadow/reported", agent.shadow().reported().build().toString());
    }

    @Test
    void buildValidEntityName() {
        B3Topic.Root entity = sut.entity("item2");
        assertEquals("b3/entity/item2/shadow/reported", entity.shadow().reported().build().toString());
    }

    @Test
    void invalidAgentIdShouldThrow() {
        assertThrows(TopicNameValidationException.class, () -> {
            sut.agent("id/part");
        });
    }

    @Test
    void invalidEntityIdShouldThrow() {
        assertThrows(TopicNameValidationException.class, () -> {
            sut.entity("id/part");
        });
    }

    @Test
    void parseEntityReportedTopic() {
        B3Topic.Valid name = sut.parse("b3/entity/device3/shadow/reported");
        assertEquals("b3/entity/device3/shadow/reported", name.build().toString());
    }

    @Test
    void parseEntityCommandTopic() {
        B3Topic.Valid name = sut.parse("b3/entity/device3/command");
        assertEquals("b3/entity/device3/command", name.build().toString());
    }

    @Test
    void parseEntityDesiredRoleTopic() {
        B3Topic.Valid name = sut.parse("b3/entity/device3/shadow/desired/myRole");
        assertEquals("b3/entity/device3/shadow/desired/myRole", name.build().toString());
    }

    @Test
    void parseEntityCommandRoleTopic() {
        B3Topic.Valid name = sut.parse("b3/entity/device3/command/myRole");
        assertEquals("b3/entity/device3/command/myRole", name.build().toString());
    }

    @Test
    void parseAgentReportedLiveTopic() {
        B3Topic.Valid name = sut.parse("b3/agent/device1/shadow/reported/live");
        assertEquals("b3/agent/device1/shadow/reported/live", name.build().toString());
    }

    @Test
    void parseAgentReportedBatchTopic() {
        B3Topic.Valid name = sut.parse("b3/agent/device2/shadow/reported/batch");
        assertEquals("b3/agent/device2/shadow/reported/batch", name.build().toString());
    }

    @Test
    void parseAgentDesiredBatchRoleTopic() {
        B3Topic.Valid name = sut.parse("b3/agent/device4/shadow/desired/batch/myBatchRole");
        assertEquals("b3/agent/device4/shadow/desired/batch/myBatchRole", name.build().toString());
    }

    @Test
    void parseAgentReportedTopic() {
        B3Topic.Valid name = sut.parse("b3/agent/device3/shadow/reported");
        assertEquals("b3/agent/device3/shadow/reported", name.build().toString());
    }

    @Test
    void parseAgentCommandTopic() {
        B3Topic.Valid name = sut.parse("b3/agent/device3/command");
        assertEquals("b3/agent/device3/command", name.build().toString());
    }

    @Test
    void parseAgentDesiredRoleTopic() {
        B3Topic.Valid name = sut.parse("b3/agent/device3/shadow/desired/myRole");
        assertEquals("b3/agent/device3/shadow/desired/myRole", name.build().toString());
    }

    @Test
    void parseAgentCommandRoleTopic() {
        B3Topic.Valid name = sut.parse("b3/agent/agent1/command/myCommand");
        assertEquals("b3/agent/agent1/command/myCommand", name.build().toString());
    }

    @Test
    void invalidTransitionShouldThrowOnMissplaced() {
        assertThrows(TopicNameValidationException.class, () -> {
            sut.parse("b3/entity/device5/shadow/entity");
        });
    }

    @Test
    void invalidTransitionShouldThrowOnUnknown() {
        assertThrows(TopicNameValidationException.class, () -> {
            sut.parse("b3/entity/device5/shadow/unknown");
        });
    }

    @Test
    void invalidCategoryShouldThrow() {
        assertThrows(TopicNameValidationException.class, () -> {
            sut.parse("b3/invalid/device6/shadow/reported/live");
        });
    }

    @Test
    void tooShortTopicShouldThrow() {
        assertThrows(TopicNameValidationException.class, () -> {
            sut.parse("b3/entity/device7");
        });
    }
}
