package io.tinga.b3.agent.security.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javafaker.Faker;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import io.tinga.b3.agent.security.Operation;
import io.tinga.b3.helpers.AgentProxy;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.helpers.JsonSchemaProvider;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.TestB3TopicFactory;
import io.tinga.belt.helpers.JsonUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JsonSchemaOperationGrantsCheckerTest {

    private static final B3Topic.Factory topicFactory = TestB3TopicFactory.instance();
    private static final Faker faker = new Faker();
    private static final String agentId = faker.lorem().word();
    private static final String roleName = faker.lorem().word();
    private static final B3Topic.Base topicBase = topicFactory.agent(agentId);
    private static final B3Topic topic = topicBase.shadow().desired(roleName).build();

    @Mock
    private JsonSchemaProvider schemaProvider;

    @Mock
    private JsonUtils json;

    @Mock
    private ObjectNode reportedMsgBody;

    @Mock
    private ObjectNode operationMsgBody;

    @Mock
    private ObjectNode diff;

    @Mock
    private JsonSchema jsonSchema;

    @Mock
    private AgentProxy<GenericB3Message> agentProxy;

    @Mock
    private GenericB3Message reportedMsg;

    @Mock
    private GenericB3Message operationMsg;

    @Mock
    private Operation<GenericB3Message> operation;

    @InjectMocks
    private JsonSchemaOperationGrantsChecker<GenericB3Message> sut;


    @Test
    void shouldAllowWhenSchemaValidationPasses() throws Exception {
        when(operation.sourceTopic()).thenReturn(topic);
        when(operation.message()).thenReturn(operationMsg);
        when(operationMsg.getBody()).thenReturn(operationMsgBody);
        when(reportedMsg.getBody()).thenReturn(reportedMsgBody);
        when(json.subtract(reportedMsgBody, operationMsgBody)).thenReturn(diff);
        when(schemaProvider.getSchemaFor(topic)).thenReturn(jsonSchema);
        when(jsonSchema.validate(diff)).thenReturn(Collections.emptySet());

        sut.bind(topicBase, roleName);
        sut.handle(topic, reportedMsg);
        boolean allowed = sut.isAllowed(operation);

        assertThat(allowed).isTrue();
    }

    @Test
    void shouldBlockWhenSchemaValidationFails() throws Exception {
        ValidationMessage validationMessage = mock(ValidationMessage.class);
        when(validationMessage.getMessageKey()).thenReturn("key");
        when(validationMessage.getError()).thenReturn("some error");

        when(reportedMsg.getBody()).thenReturn(reportedMsgBody);
        when(operation.message()).thenReturn(operationMsg);
        when(operationMsg.getBody()).thenReturn(operationMsgBody);
        when(json.subtract(reportedMsgBody, operationMsgBody)).thenReturn(diff);
        when(operation.sourceTopic()).thenReturn(topic);
        when(schemaProvider.getSchemaFor(topic)).thenReturn(jsonSchema);
        when(jsonSchema.validate(diff)).thenReturn(Set.of(validationMessage));

        sut.bind(topicBase, roleName);
        sut.handle(topic, reportedMsg);
        boolean allowed = sut.isAllowed(operation);

        assertThat(allowed).isFalse();
    }

    @Test
    void shouldBlockWhenNoSchemaIsFound() throws Exception {
        when(reportedMsg.getBody()).thenReturn(reportedMsgBody);
        when(operation.message()).thenReturn(operationMsg);
        when(operationMsg.getBody()).thenReturn(operationMsgBody);
        when(json.subtract(reportedMsgBody, operationMsgBody)).thenReturn(diff);
        when(operation.sourceTopic()).thenReturn(topic);
        when(schemaProvider.getSchemaFor(topic)).thenReturn(null);

        sut.bind(topicBase, roleName);
        sut.handle(topic, reportedMsg);
        boolean allowed = sut.isAllowed(operation);

        assertThat(allowed).isFalse();
    }

    @Test
    void shouldSubscribeOnBind() {
        sut.bind(topicBase, roleName);

        verify(agentProxy).subscribe(sut);
    }

    @Test
    void getNameShouldReturnClassName() {
        assertThat(sut.getName()).isEqualTo(JsonSchemaOperationGrantsChecker.class.getSimpleName());
    }

    @Test
    void handleShouldUpdateCurrentReported() throws Exception {
        sut.bind(topicBase, roleName);
        boolean result = sut.handle(topic, reportedMsg);
        assertThat(result).isTrue();
    }
}
