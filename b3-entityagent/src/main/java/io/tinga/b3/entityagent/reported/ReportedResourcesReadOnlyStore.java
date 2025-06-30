package io.tinga.b3.entityagent.reported;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import io.tinga.b3.entityagent.EntityConfig;
import io.tinga.b3.entityagent.operation.EntityMessage;

public class ReportedResourcesReadOnlyStore implements ReportedStore {

    @Inject
    private EntityConfig config;

    @Inject
    private ObjectMapper om;
    
    private CompletableFuture<Integer> initialization;
    private JsonNode cache;

    @Override
    public Future<Integer> init() {
        if(this.initialization != null) {
            return this.initialization;
        }

        this.initialization = new CompletableFuture<>();

        new Thread(() -> {
            try {
                InputStream fis = ReportedResourcesReadOnlyStore.class.getResourceAsStream(config.getReportedStoreRef());
                cache = om.readTree(fis);
                initialization.complete(1);
            } catch (IOException e) {
                initialization.completeExceptionally(e);
            }
        }).start();

        return this.initialization;
    }

    @Override
    public boolean isInitialized() {
        return this.initialization != null && this.initialization.isDone();
    }

    @Override
    public EntityMessage read(String topicName) {
        if(this.cache != null && this.cache.has(topicName)) {
            JsonNode item = this.cache.get(topicName);
            return this.om.convertValue(item, EntityMessage.class);
        }
        return null;
    }

    @Override
    public EntityMessage update(String topicName, EntityMessage newValue) {
        throw new UnsupportedOperationException("Read only entities store");
    }

}
