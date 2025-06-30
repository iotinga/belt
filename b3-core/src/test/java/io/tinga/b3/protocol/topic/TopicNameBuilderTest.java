package io.tinga.b3.protocol.topic;

import static io.tinga.b3.protocol.topic.TopicName.GLUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;

import io.tinga.b3.protocol.TopicNameValidationException;

public class TopicNameBuilderTest {
    String fakeId;
    TopicNameFactory factory;
    String roleString;
    String customRoot;
    String defaultRoot;

    @BeforeEach
    void setUp() {
        Faker faker = new Faker();

        factory = new BasicTopicNameFactory();
        fakeId = faker.lorem().word();
        customRoot = faker.lorem().word();
        roleString = faker.lorem().word();
    }


    @Test
    public void customBuilderWithLastGlueWordTest() {
        factory = new BasicTopicNameFactory(customRoot + GLUE);
        String expectedResult = customRoot + "/agent/" + fakeId + "/shadow/reported";
        TopicName topic = factory.root().agent(fakeId).shadow().reported();
        String result = topic.build();
//        System.out.println(result);
        assertEquals(expectedResult, result);
    }

    @Test
    public void agentShadowReportedTest() {
        // 1
        String expectedResult = defaultRoot + "/agent/" + fakeId + "/shadow/reported";
        String result = factory.root().agent(fakeId).shadow().reported().build();
        assertEquals(expectedResult, result);

    }


    @Test
    public void agentShadowDesiredRoleTest() {
        // 2
        String expectedResult = defaultRoot + "/agent/" + fakeId + "/shadow/desired/" + roleString;
        String result = factory.root().agent(fakeId).shadow().desired(roleString).build();
        assertEquals(expectedResult, result);

    }

    @Test
    public void agentShadowDesiredRoleStringTest() {
        // 2 stringRole
        String expectedResult = defaultRoot + "/agent/" + fakeId + "/shadow/desired/" + roleString;
        String result = factory.root().agent(fakeId).shadow().desired(roleString).build();
        assertEquals(expectedResult, result);
    }

    @Test
    public void agentShadowDesiredRoleStringWithGlueWordTest() {
        // 2 glue on stringRole
        String invalidRole = roleString + GLUE;
//        System.out.println(invalidRole);
        assertThrows(TopicNameValidationException.class, () -> factory.root().agent(fakeId).shadow().desired(invalidRole));
    }


    @Test
    public void agentShadowReportedBatchTest() {
        // 3
        String expectedResult = defaultRoot + "/agent/" + fakeId + "/shadow/reported/batch";
        String result = factory.root().agent(fakeId).shadow().reported().batch().build();
        assertEquals(expectedResult, result);

    }

    @Test
    public void agentShadowDesiredBatchRoleTest() {
        // 4
        String expectedResult = defaultRoot + "/agent/" + fakeId + "/shadow/desired/batch/" + roleString;
        String result = factory.root().agent(fakeId).shadow().desired().batch(roleString).build();
        assertEquals(expectedResult, result);

    }

    @Test
    public void agentShadowDesiredBatchRoleStringTest() {
        // 4 stringRole
        String expectedResult = defaultRoot + "/agent/" + fakeId + "/shadow/desired/batch/" + roleString;
        String result = factory.root().agent(fakeId).shadow().desired().batch(roleString).build();
        assertEquals(expectedResult, result);

    }


    @Test
    public void agentShadowDesiredBatchRoleStringWithGlueWordTest() {
        // 4 glue on stringRole
        String invalidRole = roleString + GLUE;
//        System.out.println(invalidRole);
        assertThrows(TopicNameValidationException.class, () -> factory.root().agent(fakeId).shadow().desired().batch(invalidRole));
    }


    @Test
    public void agentShadowReportedLiveTest() {
        // 5
        String expectedResult = defaultRoot + "/agent/" + fakeId + "/shadow/reported/live";
        String result = factory.root().agent(fakeId).shadow().reported().live().build();
        assertEquals(expectedResult, result);

    }

    @Test
    public void agentCommandTest() {
        // 6
        String expectedResult = defaultRoot + "/agent/" + fakeId + "/command";
        String result = factory.root().agent(fakeId).command().build();
        assertEquals(expectedResult, result);

    }

    @Test
    public void agentCommandRoleTest() {
        // 7
        String expectedResult = defaultRoot + "/agent/" + fakeId + "/command/" + roleString;
        String result = factory.root().agent(fakeId).command(roleString).build();
        assertEquals(expectedResult, result);

    }

    @Test
    public void agentCommandRoleStringTest() {
        // 7 stringRole
        String expectedResult = defaultRoot + "/agent/" + fakeId + "/command/" + roleString;
        String result = factory.root().agent(fakeId).command(roleString).build();
        assertEquals(expectedResult, result);

    }


    @Test
    public void agentCommandRoleStringWithGlueWordTest() {
        // 7 glue on stringRole
        String invalidRole = roleString + GLUE;
//        System.out.println(invalidRole);
        assertThrows(TopicNameValidationException.class, () -> factory.root().agent(fakeId).command(invalidRole));
    }


    @Test
    public void entityShadowReportedTest() {
        // 1
        String expectedResult = defaultRoot + "/entity/" + fakeId + "/shadow/reported";
        String result = factory.root().entity(fakeId).shadow().reported().build();
        assertEquals(expectedResult, result);

    }

    @Test
    public void entityShadowDesiredRoleTest() {
        // 2
        String expectedResult = defaultRoot + "/entity/" + fakeId + "/shadow/desired/" + roleString;
        String result = factory.root().entity(fakeId).shadow().desired(roleString).build();
        assertEquals(expectedResult, result);

    }

    @Test
    public void entityShadowDesiredRoleStringTest() {
        // 2 stringRole
        String expectedResult = defaultRoot + "/entity/" + fakeId + "/shadow/desired/" + roleString;
        String result = factory.root().entity(fakeId).shadow().desired(roleString).build();
        assertEquals(expectedResult, result);

    }


    @Test
    public void entityShadowDesiredRoleStringWithGlueWordTest() {
        // 2 glue on stringRole
        String invalidRole = roleString + GLUE;
        assertThrows(TopicNameValidationException.class, () -> factory.root().entity(fakeId).shadow().desired(invalidRole));
    }


    @Test
    public void entityCommandTest() {
        // 3
        String expectedResult = defaultRoot + "/entity/" + fakeId + "/command";
        String result = factory.root().entity(fakeId).command().build();
        assertEquals(expectedResult, result);

    }

    @Test
    public void entityCommandRoleTest() {
        // 4
        String expectedResult = defaultRoot + "/entity/" + fakeId + "/command/" + roleString;
        String result = factory.root().entity(fakeId).command(roleString).build();
        assertEquals(expectedResult, result);

    }

    @Test
    public void entityCommandRoleStringTest() {
        // 4 stringRole
        String expectedResult = defaultRoot + "/entity/" + fakeId + "/command/" + roleString;
        String result = factory.root().entity(fakeId).command(roleString).build();
        assertEquals(expectedResult, result);

    }


    @Test
    public void entityCommandRoleStringWithGlueWordTest() {
        // 4 glue on stringRole
        String invalidRole = roleString + GLUE;
//        System.out.println(invalidRole);
        assertThrows(TopicNameValidationException.class, () -> factory.root().entity(fakeId).command(invalidRole));
    }


    @Test
    public void invalidAgentIdTest() {
        String invalidId = fakeId + GLUE + fakeId;
        assertThrows(TopicNameValidationException.class, () -> factory.root().agent(invalidId));

    }

    @Test
    public void invalidEntityIdWithGlueTest() {
        String invalidId = fakeId + GLUE + fakeId;
        assertThrows(TopicNameValidationException.class, () -> factory.root().entity(invalidId));
    }


}
