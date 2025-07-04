package io.tinga.b3.protocol;

import static org.assertj.core.api.Assertions.assertThat;

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
                faker.lorem().word(),
                Status.OK, // assuming Status enum has SUCCESS
                randomBody()
        );
    }

    @Test
    void testSetters() {
        B3Message<String> message = new B3Message<>();

        message.setTimestamp(1700000000000L);
        message.setVersion(3);
        message.setProtocolVersion(2);
        message.setMethod("corr-id-999");
        message.setStatus(Status.OK);
        message.setBody("payload");

        assertThat(message.getTimestamp()).isEqualTo(1700000000000L);
        assertThat(message.getVersion()).isEqualTo(3);
        assertThat(message.getProtocolVersion()).isEqualTo(2);
        assertThat(message.getCorrelationId()).isEqualTo("corr-id-999");
        assertThat(message.getStatus()).isEqualTo(Status.OK);
        assertThat(message.getBody()).isEqualTo("payload");
    }

    @Test
    void testResponseCreatesNewMessageWithCorrectFields() {
        B3Message<String> original = new B3Message<>(
            1600000000000L,
            1,
            7,
            "corr-id-001",
            Status.OK,
            "original-body"
        );

        B3Message<String> response = original.response(
            1800000000000L,
            Status.BAD_GATEWAY,
            9,
            "response-body"
        );

        // Il response deve mantenere protocolVersion e correlationId dell'originale
        assertThat(response.getProtocolVersion()).isEqualTo(7);
        assertThat(response.getCorrelationId()).isEqualTo("corr-id-001");

        // Ma sostituire timestamp, version, status e body
        assertThat(response.getTimestamp()).isEqualTo(1800000000000L);
        assertThat(response.getVersion()).isEqualTo(9);
        assertThat(response.getStatus()).isEqualTo(Status.BAD_GATEWAY);
        assertThat(response.getBody()).isEqualTo("response-body");
    }

    @Test
    void testToStringWithAllFields() {
        B3Message<String> message = new B3Message<>(
            1690000000000L,
            2,
            1,
            "abc123",
            Status.OK,
            "body content"
        );

        String result = message.toString();

        assertThat(result)
            .startsWith("1690000000000[1]:abc123 - OK - v2")
            .endsWith("[...]");
    }

    @Test
    void testToStringWithNullBody() {
        B3Message<String> message = new B3Message<>(
            1690000000000L,
            2,
            1,
            "abc123",
            Status.BAD_GATEWAY,
            null
        );

        String result = message.toString();

        assertThat(result)
            .startsWith("1690000000000[1]:abc123 - BAD_GATEWAY - v2")
            .endsWith("[NULL]");
    }

    @Test
    void testToStringWithNullVersion() {
        B3Message<String> message = new B3Message<>(
            1690000000000L,
            null,
            1,
            "abc123",
            Status.OK,
            "some body"
        );

        String result = message.toString();

        assertThat(result)
            .startsWith("1690000000000[1]:abc123 - OK - v[NULL]")
            .endsWith("[...]");
    }

    @Test
    void testToStringWithNullBodyAndNullVersion() {
        B3Message<String> message = new B3Message<>(
            1690000000000L,
            null,
            1,
            "abc123",
            Status.BAD_GATEWAY,
            null
        );

        String result = message.toString();

        assertThat(result)
            .isEqualTo("1690000000000[1]:abc123 - BAD_GATEWAY - v[NULL] [NULL]");
    }

    @Test
    void equals_sameInstance_shouldBeTrue() {
        B3Message<ObjectNode> msg = createRandomMessage();
        boolean result = msg.equals(msg);
        assertTrue(result);
    }

    @Test
    void equals_null_shouldBeFalse() {
        B3Message<ObjectNode> msg = createRandomMessage();
        boolean result = msg.equals(null);
        assertFalse(result);
    }

    @Test
    void equals_differentType_shouldBeFalse() {
        B3Message<ObjectNode> msg = createRandomMessage();
        boolean result = msg.equals("pippo");
        assertFalse(result);
    }

    @Test
    void equals_equalFields_shouldBeTrue() {
        Long ts = faker.number().randomNumber();
        Integer v = faker.number().numberBetween(1, 10);
        Integer pv = faker.number().numberBetween(1, 5);
        String a = faker.lorem().word();
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
                m1.getCorrelationId(),
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
                m1.getCorrelationId(),
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
                m1.getCorrelationId(),
                m1.getStatus(),
                m1.getBody()
        );
        assertNotEquals(m1, m2);
    }

    @Test
    void equals_differentAction_shouldBeFalse() {
        ObjectNode body = randomBody();
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(1L, 1, 1, faker.lorem().word(), Status.OK, body);
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(1L, 1, 1, faker.lorem().word(), Status.OK, body);
        assertNotEquals(m1, m2);
    }

    @Test
    void equals_differentStatus_shouldBeFalse() {
        ObjectNode body = randomBody();
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(1L, 1, 1, faker.lorem().word(), Status.OK, body);
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(1L, 1, 1, m1.getCorrelationId(), Status.BAD_GATEWAY, body);
        assertNotEquals(m1, m2);
    }

    @Test
    void equals_differentBody_shouldBeFalse() {
        ObjectNode body1 = mapper.createObjectNode().put("x", "1");
        ObjectNode body2 = mapper.createObjectNode().put("x", "2");

        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(1L, 1, 1, faker.lorem().word(), Status.OK, body1);
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(1L, 1, 1, m1.getCorrelationId(), Status.OK, body2);

        assertNotEquals(m1, m2);
    }

    @Test
    void equals_nullTimestamp_shouldBeEqualIfBothNull() {
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(null, 1, 1,faker.lorem().word(), Status.OK, randomBody());
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(null, 1, 1, m1.getCorrelationId(), Status.OK, randomBody());
        // force equal bodies for test
        m2 = new B3Message<ObjectNode>(null, 1, 1, m1.getCorrelationId(), Status.OK, m1.getBody());
        assertEquals(m1, m2);
    }

    @Test
    void equals_nullVersion_shouldBeEqualIfBothNull() {
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(1L, null, 1, faker.lorem().word(), Status.OK, randomBody());
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(1L, null, 1, m1.getCorrelationId(), Status.OK, m1.getBody());
        assertEquals(m1, m2);
    }

    @Test
    void equals_nullProtocolVersion_shouldBeEqualIfBothNull() {
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(1L, 1, null, faker.lorem().word(), Status.OK, randomBody());
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(1L, 1, null, m1.getCorrelationId(), Status.OK, m1.getBody());
        assertEquals(m1, m2);
    }

    @Test
    void equals_nullBody_shouldBeEqualIfBothNull() {
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(1L, 1, 1, faker.lorem().word(), Status.OK, null);
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(1L, 1, 1, m1.getCorrelationId(), Status.OK, null);
        assertEquals(m1, m2);
    }

    @Test
    void equals_nullTimestamp_shouldNotEqualNonNull() {
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(null, 1, 1, faker.lorem().word(), Status.OK, randomBody());
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(123L, 1, 1, m1.getCorrelationId(), Status.OK, m1.getBody());
        assertNotEquals(m1, m2);
    }

    @Test
    void equals_nullVersion_shouldNotEqualNonNull() {
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(1L, null, 1, faker.lorem().word(), Status.OK, randomBody());
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(1L, 9, 1, m1.getCorrelationId(), Status.OK, m1.getBody());
        assertNotEquals(m1, m2);
    }

    @Test
    void equals_nullProtocolVersion_shouldNotEqualNonNull() {
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(1L, 1, null, faker.lorem().word(), Status.OK, randomBody());
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(1L, 1, 42, m1.getCorrelationId(), Status.OK, m1.getBody());
        assertNotEquals(m1, m2);
    }

    @Test
    void equals_nullBody_shouldNotEqualNonNull() {
        B3Message<ObjectNode> m1 = new B3Message<ObjectNode>(1L, 1, 1, faker.lorem().word(), Status.OK, null);
        B3Message<ObjectNode> m2 = new B3Message<ObjectNode>(1L, 1, 1, m1.getCorrelationId(), Status.OK, randomBody());
        assertNotEquals(m1, m2);
    }
}
