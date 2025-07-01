package io.tinga.b3.entityagent.reported;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import com.google.inject.Inject;

import io.tinga.b3.entityagent.EntityConfig;
import io.tinga.b3.protocol.GenericB3Message;
import it.netgrid.bauer.EventHandler;
import it.netgrid.bauer.ITopicFactory;
import it.netgrid.bauer.Topic;

public class ReportedRetainedMessagesStore implements ReportedStore, EventHandler<GenericB3Message> {

    @Inject
    private EntityConfig config;

    @Inject
    private ITopicFactory topicFactory;

    private Topic<GenericB3Message> topic;
    private CompletableFuture<Integer> initialization;
    private final Map<String, GenericB3Message> cache = new HashMap<>();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Class<GenericB3Message> getEventClass() {
        return GenericB3Message.class;
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
    public boolean handle(String topic, GenericB3Message event) throws Exception {
        this.cache.put(topic, event);
        return true;
    }

    @Override
    public synchronized GenericB3Message read(String topicName) {
        return this.cache.get(topicName);
    }

    @Override
    public GenericB3Message update(String topicName, GenericB3Message newValue) {
        GenericB3Message current = this.cache.get(topicName);
        this.cache.put(topicName, newValue);
        return current;
    }

}
