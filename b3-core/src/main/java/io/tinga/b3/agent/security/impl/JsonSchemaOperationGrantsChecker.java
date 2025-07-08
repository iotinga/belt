package io.tinga.b3.agent.security.impl;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;

import io.tinga.b3.agent.security.Operation;
import io.tinga.b3.helpers.AgentProxy;
import io.tinga.b3.helpers.JsonSchemaProvider;
import io.tinga.b3.protocol.B3EventHandler;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.belt.helpers.JsonUtils;

public class JsonSchemaOperationGrantsChecker<M extends B3Message<? extends JsonNode>>
        implements Operation.GrantsChecker<M>, B3EventHandler<M> {

    private static final Logger log = LoggerFactory.getLogger(JsonSchemaOperationGrantsChecker.class);

    protected final JsonUtils json;
    protected final JsonSchemaProvider schemaProvider;
    private final AgentProxy.Factory<M> agentProxyFactory;
    private AgentProxy<M> agentProxy;

    private M currentReported;

    @Inject
    public JsonSchemaOperationGrantsChecker(JsonSchemaProvider schemaProvider,
            AgentProxy.Factory<M> agentProxyFactory, JsonUtils json) {
        this.json = json;
        this.agentProxyFactory = agentProxyFactory;
        this.schemaProvider = schemaProvider;
    }

    @Override
    public boolean isAllowed(Operation<M> operation) {
        M reported = this.currentReported;

        JsonNode oldBody = reported == null ? null : reported.getBody();
        JsonNode newBody = operation.message() == null ? null : operation.message().getBody();
        JsonNode diff = json.subtract(oldBody,newBody);
        JsonSchema schema = this.schemaProvider.getSchemaFor(operation.sourceTopic());

        if (schema != null) {
            Set<ValidationMessage> validationResult = schema.validate(diff);
            if (validationResult.size() == 0) {
                log.debug(String.format("[ALLOWED] %s", operation.sourceTopic()));
                return true;
            } else {
                log.warn(String.format("%s: blocked %s", operation.sourceTopic(), diff));
                for (ValidationMessage message : validationResult) {
                    log.info(String.format("REASON %s: %s", message.getMessageKey(), message.getError()));
                }
            }
        }

        log.debug(String.format("[NO SCHEMA] %s", operation.sourceTopic()));
        return false;
    }

    @Override
    public void bind(B3Topic.Base topicBase, String roleName) {
        this.agentProxy = this.agentProxyFactory.getProxy(topicBase, roleName);
        this.agentProxy.subscribe(this);
    }

    @Override
    public String getName() {
        return JsonSchemaOperationGrantsChecker.class.getSimpleName();
    }

    @Override
    public boolean handle(B3Topic topic, M event) throws Exception {
        if (this.currentReported == null) {
            this.currentReported = event;
            this.agentProxy.unsubscribe(this);
        }
        return true;
    }
}