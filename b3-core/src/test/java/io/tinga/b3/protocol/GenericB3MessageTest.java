package io.tinga.b3.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javafaker.Faker;
import io.tinga.belt.output.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenericB3MessageTest {

    private final Faker faker = new Faker();
    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
    }

    private ObjectNode randomBody() {
        ObjectNode node = mapper.createObjectNode();
        node.put("key", faker.lorem().word());
        return node;
    }

    private GenericB3Message createRandomMessage() {
        return new GenericB3Message(
                faker.number().randomNumber(),
                faker.number().numberBetween(1, 10),
                faker.number().numberBetween(1, 5),
                Action.GET, // assuming Action enum has GET
                Status.OK, // assuming Status enum has SUCCESS
                randomBody()
        );
    }

    @Test
    void equals_sameInstance_shouldBeTrue() {
        GenericB3Message msg = createRandomMessage();
        assertEquals(msg, msg);
    }

    @Test
    void equals_null_shouldBeFalse() {
        GenericB3Message msg = createRandomMessage();
        assertNotEquals(null, msg);
    }

    @Test
    void equals_differentType_shouldBeFalse() {
        GenericB3Message msg = createRandomMessage();
        assertNotEquals("a string", msg);
    }

    @Test
    void equals_equalFields_shouldBeTrue() {
        Long ts = faker.number().randomNumber();
        Integer v = faker.number().numberBetween(1, 10);
        Integer pv = faker.number().numberBetween(1, 5);
        Action a = Action.GET;
        Status s = Status.OK;
        ObjectNode b = randomBody();

        GenericB3Message m1 = new GenericB3Message(ts, v, pv, a, s, b);
        GenericB3Message m2 = new GenericB3Message(ts, v, pv, a, s, b);

        assertEquals(m1, m2);
    }

    @Test
    void equals_differentTimestamp_shouldBeFalse() {
        GenericB3Message m1 = createRandomMessage();
        GenericB3Message m2 = new GenericB3Message(
                m1.getTimestamp() + 1,
                m1.getVersion(),
                m1.getProtocolVersion(),
                m1.getAction(),
                m1.getStatus(),
                m1.getBody()
        );
        assertNotEquals(m1, m2);
    }

    @Test
    void equals_differentVersion_shouldBeFalse() {
        GenericB3Message m1 = createRandomMessage();
        GenericB3Message m2 = new GenericB3Message(
                m1.getTimestamp(),
                m1.getVersion() + 1,
                m1.getProtocolVersion(),
                m1.getAction(),
                m1.getStatus(),
                m1.getBody()
        );
        assertNotEquals(m1, m2);
    }

    @Test
    void equals_differentProtocolVersion_shouldBeFalse() {
        GenericB3Message m1 = createRandomMessage();
        GenericB3Message m2 = new GenericB3Message(
                m1.getTimestamp(),
                m1.getVersion(),
                m1.getProtocolVersion() + 1,
                m1.getAction(),
                m1.getStatus(),
                m1.getBody()
        );
        assertNotEquals(m1, m2);
    }

    @Test
    void equals_differentAction_shouldBeFalse() {
        ObjectNode body = randomBody();
        GenericB3Message m1 = new GenericB3Message(1L, 1, 1, Action.GET, Status.OK, body);
        GenericB3Message m2 = new GenericB3Message(1L, 1, 1, Action.PUT, Status.OK, body);
        assertNotEquals(m1, m2);
    }

    @Test
    void equals_differentStatus_shouldBeFalse() {
        ObjectNode body = randomBody();
        GenericB3Message m1 = new GenericB3Message(1L, 1, 1, Action.GET, Status.OK, body);
        GenericB3Message m2 = new GenericB3Message(1L, 1, 1, Action.GET, Status.BAD_GATEWAY, body);
        assertNotEquals(m1, m2);
    }

    @Test
    void equals_differentBody_shouldBeFalse() {
        ObjectNode body1 = mapper.createObjectNode().put("x", "1");
        ObjectNode body2 = mapper.createObjectNode().put("x", "2");

        GenericB3Message m1 = new GenericB3Message(1L, 1, 1, Action.GET, Status.OK, body1);
        GenericB3Message m2 = new GenericB3Message(1L, 1, 1, Action.GET, Status.OK, body2);

        assertNotEquals(m1, m2);
    }

    @Test
    void equals_nullTimestamp_shouldBeEqualIfBothNull() {
        GenericB3Message m1 = new GenericB3Message(null, 1, 1, Action.GET, Status.OK, randomBody());
        GenericB3Message m2 = new GenericB3Message(null, 1, 1, Action.GET, Status.OK, randomBody());
        // force equal bodies for test
        m2 = new GenericB3Message(null, 1, 1, Action.GET, Status.OK, m1.getBody());
        assertEquals(m1, m2);
    }

    @Test
    void equals_nullVersion_shouldBeEqualIfBothNull() {
        GenericB3Message m1 = new GenericB3Message(1L, null, 1, Action.GET, Status.OK, randomBody());
        GenericB3Message m2 = new GenericB3Message(1L, null, 1, Action.GET, Status.OK, m1.getBody());
        assertEquals(m1, m2);
    }

    @Test
    void equals_nullProtocolVersion_shouldBeEqualIfBothNull() {
        GenericB3Message m1 = new GenericB3Message(1L, 1, null, Action.GET, Status.OK, randomBody());
        GenericB3Message m2 = new GenericB3Message(1L, 1, null, Action.GET, Status.OK, m1.getBody());
        assertEquals(m1, m2);
    }

    @Test
    void equals_nullBody_shouldBeEqualIfBothNull() {
        GenericB3Message m1 = new GenericB3Message(1L, 1, 1, Action.GET, Status.OK, null);
        GenericB3Message m2 = new GenericB3Message(1L, 1, 1, Action.GET, Status.OK, null);
        assertEquals(m1, m2);
    }

    @Test
    void equals_nullTimestamp_shouldNotEqualNonNull() {
        GenericB3Message m1 = new GenericB3Message(null, 1, 1, Action.GET, Status.OK, randomBody());
        GenericB3Message m2 = new GenericB3Message(123L, 1, 1, Action.GET, Status.OK, m1.getBody());
        assertNotEquals(m1, m2);
    }

    @Test
    void equals_nullVersion_shouldNotEqualNonNull() {
        GenericB3Message m1 = new GenericB3Message(1L, null, 1, Action.GET, Status.OK, randomBody());
        GenericB3Message m2 = new GenericB3Message(1L, 9, 1, Action.GET, Status.OK, m1.getBody());
        assertNotEquals(m1, m2);
    }

    @Test
    void equals_nullProtocolVersion_shouldNotEqualNonNull() {
        GenericB3Message m1 = new GenericB3Message(1L, 1, null, Action.GET, Status.OK, randomBody());
        GenericB3Message m2 = new GenericB3Message(1L, 1, 42, Action.GET, Status.OK, m1.getBody());
        assertNotEquals(m1, m2);
    }

    @Test
    void equals_nullBody_shouldNotEqualNonNull() {
        GenericB3Message m1 = new GenericB3Message(1L, 1, 1, Action.GET, Status.OK, null);
        GenericB3Message m2 = new GenericB3Message(1L, 1, 1, Action.GET, Status.OK, randomBody());
        assertNotEquals(m1, m2);
    }
}
