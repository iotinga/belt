package io.tinga.b3.core.shadowing.reported;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import io.tinga.b3.core.Agent.LocalShadowingConfig;
import io.tinga.b3.protocol.GenericB3Message;

public class ReportedFromFileReadOnlyStore implements ReportedStore {

    @Inject
    private LocalShadowingConfig config;

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
                InputStream fis = new FileInputStream(this.config.getReportedStoreRef());
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
    public GenericB3Message read(String topicName) {
        if(this.cache != null && this.cache.has(topicName)) {
            JsonNode item = this.cache.get(topicName);
            return this.om.convertValue(item, GenericB3Message.class);
        }
        return null;
    }

    @Override
    public GenericB3Message update(String topicName, GenericB3Message newValue) {
        throw new UnsupportedOperationException("Read only entities store");
    }

}
