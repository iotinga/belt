package io.tinga.b3.protocol.topic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import io.tinga.b3.protocol.TopicNameValidationException;


@ExtendWith(MockitoExtension.class)
public class B3TopicFactoryImplTest {

    @InjectMocks B3TopicFactoryImpl sut;

    @Test
    void customRoot() {
        sut = new B3TopicFactoryImpl("test");
        B3Topic agent = sut.agent("item1");
        assertEquals("test/agent/item1/shadow/reported", agent.shadow().reported().build());
    }

    @Test
    void removesLeadingGlueFromCustomRoot() {
        sut = new B3TopicFactoryImpl("test/");
        B3Topic agent = sut.agent("item1");
        assertEquals("test/agent/item1/shadow/reported", agent.shadow().reported().build());
    }
    @Test
    void defaultsOnNullCustomRoot() {
        sut = new B3TopicFactoryImpl(null);
        B3Topic agent = sut.agent("item1");
        assertEquals("b3/agent/item1/shadow/reported", agent.shadow().reported().build());
    }


    @Test
    void buildValidAgentName() {
        B3Topic agent = sut.agent("item1");
        assertEquals("b3/agent/item1/shadow/reported", agent.shadow().reported().build());
    }

    @Test
    void buildValidEntityName() {
        B3Topic entity = sut.entity("item2");
        assertEquals("b3/entity/item2/shadow/reported", entity.shadow().reported().build());
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
        B3Topic.Name name = sut.parse("b3/entity/device3/shadow/reported");
        assertEquals("b3/entity/device3/shadow/reported", name.build());
    }

    @Test
    void parseEntityCommandTopic() {
        B3Topic.Name name = sut.parse("b3/entity/device3/command");
        assertEquals("b3/entity/device3/command", name.build());
    }

    @Test
    void parseEntityDesiredRoleTopic() {
        B3Topic.Name name = sut.parse("b3/entity/device3/shadow/desired/myRole");
        assertEquals("b3/entity/device3/shadow/desired/myRole", name.build());
    }

    @Test
    void parseEntityCommandRoleTopic() {
        B3Topic.Name name = sut.parse("b3/entity/device3/command/myRole");
        assertEquals("b3/entity/device3/command/myRole", name.build());
    }

    @Test
    void parseAgentReportedLiveTopic() {
        B3Topic.Name name = sut.parse("b3/agent/device1/shadow/reported/live");
        assertEquals("b3/agent/device1/shadow/reported/live", name.build());
    }

    @Test
    void parseAgentReportedBatchTopic() {
        B3Topic.Name name = sut.parse("b3/agent/device2/shadow/reported/batch");
        assertEquals("b3/agent/device2/shadow/reported/batch", name.build());
    }

    @Test
    void parseAgentDesiredBatchRoleTopic() {
        B3Topic.Name name = sut.parse("b3/agent/device4/shadow/desired/batch/myBatchRole");
        assertEquals("b3/agent/device4/shadow/desired/batch/myBatchRole", name.build());
    }

    @Test
    void parseAgentReportedTopic() {
        B3Topic.Name name = sut.parse("b3/agent/device3/shadow/reported");
        assertEquals("b3/agent/device3/shadow/reported", name.build());
    }

    @Test
    void parseAgentCommandTopic() {
        B3Topic.Name name = sut.parse("b3/agent/device3/command");
        assertEquals("b3/agent/device3/command", name.build());
    }

    @Test
    void parseAgentDesiredRoleTopic() {
        B3Topic.Name name = sut.parse("b3/agent/device3/shadow/desired/myRole");
        assertEquals("b3/agent/device3/shadow/desired/myRole", name.build());
    }

    @Test
    void parseAgentCommandRoleTopic() {
        B3Topic.Name name = sut.parse("b3/agent/agent1/command/myCommand");
        assertEquals("b3/agent/agent1/command/myCommand", name.build());
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
