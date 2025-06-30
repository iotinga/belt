package io.tinga.b3.entityagent.reported;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import com.google.inject.Inject;

import io.tinga.b3.entityagent.EntityConfig;
import io.tinga.b3.entityagent.operation.EntityMessage;
import it.netgrid.bauer.EventHandler;
import it.netgrid.bauer.ITopicFactory;
import it.netgrid.bauer.Topic;

public class ReportedRetainedMessagesStore implements ReportedStore, EventHandler<EntityMessage> {

    @Inject
    private EntityConfig config;

    @Inject
    private ITopicFactory topicFactory;

    private Topic<EntityMessage> topic;
    private CompletableFuture<Integer> initialization;
    private final Map<String, EntityMessage> cache = new HashMap<>();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Class<EntityMessage> getEventClass() {
        return EntityMessage.class;
    }

    @Override
    public synchronized boolean isInitialized() {
        return this.initialization != null && this.initialization.isDone();
    }

    @Override
    public synchronized Future<Integer> init() {
        if (this.initialization != null) {
            return this.initialization;
        }

        this.initialization = new CompletableFuture<>();
        this.topic = topicFactory.getTopic(this.config.getReportedTopicFilter());
        this.topic.addHandler(this);

        // Wait some seconds to allow retained messages reception
        new Thread(() -> {
            try {
                Thread.sleep(config.getRetainedStoreWaitOnInitMillis());
                initialization.complete(this.cache.size());
            } catch (InterruptedException e) {
                initialization.completeExceptionally(e);
            }
        }).start();

        return this.initialization;
    }

    @Override
    public boolean handle(String topic, EntityMessage event) throws Exception {
        this.cache.put(topic, event);
        return true;
    }

    @Override
    public synchronized EntityMessage read(String topicName) {
        return this.cache.get(topicName);
    }

    @Override
    public EntityMessage update(String topicName, EntityMessage newValue) {
        EntityMessage current = this.cache.get(topicName);
        this.cache.put(topicName, newValue);
        return current;
    }

}
