package io.tinga.b3.entityagent.operation;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;

import io.tinga.belt.helpers.JsonUtils;
import io.tinga.belt.output.GadgetSink;
import io.tinga.b3.entityagent.jsonschema.JsonSchemaProvider;
import io.tinga.b3.entityagent.reported.ReportedStore;

public class EntityOperationJsonSchemaChecker implements EntityOperationGrantsChecker {

    private static final Logger log = LoggerFactory.getLogger(EntityOperationJsonSchemaChecker.class);

    @Inject
    private JsonUtils json;

    @Inject
    private GadgetSink out;

    @Inject
    private ReportedStore store;

    @Inject
    private JsonSchemaProvider schemaProvider;

    @Override
    public boolean isAllowed(EntityOperation operation) {
        try {
            if (!this.store.isInitialized()) {
                Future<Integer> initialization = this.store.init();
                int cacheSize = initialization.get();
                log.info(String.format("cache size: %d", cacheSize));
            }

            EntityMessage reported = this.store.read(operation.reportedTopic());
            JsonNode diff = json.diff(reported == null ? null : reported.getBody(),
                    operation.message() == null ? null : operation.message().getBody());
            out.put(diff.toPrettyString());
            JsonSchema schema = this.schemaProvider.getSchemaFor(operation.desiredTopic());

            if (schema != null) {
                Set<ValidationMessage> validationResult = schema.validate(diff);
                if (validationResult.size() == 0) {
                    log.debug(String.format("[ALLOWED] %s", operation.desiredTopic()));
                    return true;
                } else {
                    log.warn(String.format("%s: blocked %s", operation.desiredTopic(), diff));
                    for (ValidationMessage message : validationResult) {
                        log.info(String.format("REASON %s: %s", message.getMessageKey(), message.getError()));
                    }
                }
            }

            log.debug(String.format("[NO SCHEMA] %s", operation.desiredTopic()));
            return false;

        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

}
