package io.tinga.b3.agent.shadowing.impl;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;

import io.tinga.belt.helpers.JsonUtils;
import io.tinga.belt.output.GadgetSink;
import io.tinga.b3.agent.shadowing.Operation;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.helpers.jsonschema.JsonSchemaProvider;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;

public class OperationJsonSchemaChecker<M extends B3Message<? extends JsonNode>> extends AbstractJsonSchemaOperationGrantsChecker<M> {

    private static final Logger log = LoggerFactory.getLogger(OperationJsonSchemaChecker.class);

    @Inject
    public OperationJsonSchemaChecker(JsonSchemaProvider schemaProvider, GadgetSink out, JsonUtils json) {
        super(schemaProvider, out, json);
    }

    @Override
    public boolean isAllowed(Operation<M> operation) {
        // try {
            // if (!this.store.isInitialized()) {
            //     Future<Integer> initialization = this.store.init();
            //     int cacheSize = initialization.get();
            //     log.info(String.format("cache size: %d", cacheSize));
            // }

            // GenericB3Message reported = this.store.read(operation.reportedTopic());
            GenericB3Message reported = null;

            
            JsonNode diff = json.diff(reported == null ? null : reported.getBody(),
                    operation.message() == null ? null : operation.message().getBody());
            out.put(diff.toPrettyString());
            JsonSchema schema = this.schemaProvider.getSchemaFor(operation.sourceTopic());

            if (schema != null) {
                Set<ValidationMessage> validationResult = schema.validate(diff);
                if (validationResult.size() == 0) {
                    log.debug(String.format("[ALLOWED] %s", operation.sourceTopic()));
                    return true;
                } else {
                    log.warn(String.format("%s: blocked %s", operation.sourceTopic().toString(), diff));
                    for (ValidationMessage message : validationResult) {
                        log.info(String.format("REASON %s: %s", message.getMessageKey(), message.getError()));
                    }
                }
            }

            log.debug(String.format("[NO SCHEMA] %s", operation.sourceTopic().toString()));
            return false;

        // } catch (InterruptedException | ExecutionException e) {
        //     return false;
        // }
    }

    @Override
    public void bindTo(B3Topic.Base topicBase) {
        
    }

}
