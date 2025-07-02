package io.tinga.b3.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javafaker.Faker;
import io.tinga.belt.output.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class B3MessageTest {

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

    private B3Message<ObjectNode> createRandomMessage() {
        return new B3Message<ObjectNode>(
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
        B3Message<ObjectNode> msg = createRandomMessage();
        assertEquals(msg, msg);
    }

    @Test
    void equals_null_shouldBeFalse() {
        B3Message<ObjectNode> msg = createRandomMessage();
        assertNotEquals(null, msg);
    }

    @Test
    void equals_differentType_shouldBeFalse() {
        B3Message<ObjectNode> msg = createRandomMessage();
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

        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(ts, v, pv, a, s, b);
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(ts, v, pv, a, s, b);

        assertEquals(m1, m2);
    }

    @Test
    void equals_differentTimestamp_shouldBeFalse() {
        B3Message<ObjectNode> m1 = createRandomMessage();
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(
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
        B3Message<ObjectNode> m1 = createRandomMessage();
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(
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
        B3Message<ObjectNode> m1 = createRandomMessage();
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(
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
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(1L, 1, 1, Action.GET, Status.OK, body);
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(1L, 1, 1, Action.PUT, Status.OK, body);
        assertNotEquals(m1, m2);
    }

    @Test
    void equals_differentStatus_shouldBeFalse() {
        ObjectNode body = randomBody();
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(1L, 1, 1, Action.GET, Status.OK, body);
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(1L, 1, 1, Action.GET, Status.BAD_GATEWAY, body);
        assertNotEquals(m1, m2);
    }

    @Test
    void equals_differentBody_shouldBeFalse() {
        ObjectNode body1 = mapper.createObjectNode().put("x", "1");
        ObjectNode body2 = mapper.createObjectNode().put("x", "2");

        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(1L, 1, 1, Action.GET, Status.OK, body1);
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(1L, 1, 1, Action.GET, Status.OK, body2);

        assertNotEquals(m1, m2);
    }

    @Test
    void equals_nullTimestamp_shouldBeEqualIfBothNull() {
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(null, 1, 1, Action.GET, Status.OK, randomBody());
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(null, 1, 1, Action.GET, Status.OK, randomBody());
        // force equal bodies for test
        m2 = new B3Message<ObjectNode>(null, 1, 1, Action.GET, Status.OK, m1.getBody());
        assertEquals(m1, m2);
    }

    @Test
    void equals_nullVersion_shouldBeEqualIfBothNull() {
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(1L, null, 1, Action.GET, Status.OK, randomBody());
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(1L, null, 1, Action.GET, Status.OK, m1.getBody());
        assertEquals(m1, m2);
    }

    @Test
    void equals_nullProtocolVersion_shouldBeEqualIfBothNull() {
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(1L, 1, null, Action.GET, Status.OK, randomBody());
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(1L, 1, null, Action.GET, Status.OK, m1.getBody());
        assertEquals(m1, m2);
    }

    @Test
    void equals_nullBody_shouldBeEqualIfBothNull() {
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(1L, 1, 1, Action.GET, Status.OK, null);
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(1L, 1, 1, Action.GET, Status.OK, null);
        assertEquals(m1, m2);
    }

    @Test
    void equals_nullTimestamp_shouldNotEqualNonNull() {
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(null, 1, 1, Action.GET, Status.OK, randomBody());
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(123L, 1, 1, Action.GET, Status.OK, m1.getBody());
        assertNotEquals(m1, m2);
    }

    @Test
    void equals_nullVersion_shouldNotEqualNonNull() {
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(1L, null, 1, Action.GET, Status.OK, randomBody());
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(1L, 9, 1, Action.GET, Status.OK, m1.getBody());
        assertNotEquals(m1, m2);
    }

    @Test
    void equals_nullProtocolVersion_shouldNotEqualNonNull() {
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(1L, 1, null, Action.GET, Status.OK, randomBody());
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(1L, 1, 42, Action.GET, Status.OK, m1.getBody());
        assertNotEquals(m1, m2);
    }

    @Test
    void equals_nullBody_shouldNotEqualNonNull() {
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(1L, 1, 1, Action.GET, Status.OK, null);
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(1L, 1, 1, Action.GET, Status.OK, randomBody());
        assertNotEquals(m1, m2);
    }
}
