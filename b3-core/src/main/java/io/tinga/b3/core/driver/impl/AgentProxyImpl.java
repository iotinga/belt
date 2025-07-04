package io.tinga.b3.core.driver.impl;

import it.netgrid.bauer.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.tinga.b3.core.Agent;
import io.tinga.b3.core.B3EventHandler;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.core.driver.AgentProxy;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.belt.helpers.AEventHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AgentProxyImpl<M extends B3Message<?>> extends AEventHandler<M> implements AgentProxy<M> {
    private static final Logger log = LoggerFactory.getLogger(AgentProxyImpl.class);

    private B3Topic.Root topicRoot;
    private String roleName;
    private Topic<M> desiredTopic;
    private Topic<M> reportedTopic;
    private final List<B3EventHandler<M>> subscribers;
    private final ITopicFactoryProxy topicFactoryProxy;
    private final B3Topic.Factory topicFactory;

    private M lastShadowReported;

    public AgentProxyImpl(
            Class<M> messageClass, ITopicFactoryProxy topicFactoryProxy, B3Topic.Factory topicFactory) {
        super(messageClass);
        this.topicFactory = topicFactory;
        this.topicFactoryProxy = topicFactoryProxy;
        this.subscribers = new CopyOnWriteArrayList<>();
        this.reportedTopic.addHandler(this);
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public synchronized void bindTo(B3Topic.Root topicRoot, String roleName) {
        if (desiredTopic == null && this.reportedTopic == null) {
            this.topicRoot = topicRoot;
            this.roleName = roleName;
            this.desiredTopic = topicFactoryProxy
                    .getTopic(topicRoot.shadow().desired(roleName).build(), true);
            this.reportedTopic = topicFactoryProxy.getTopic(topicRoot.shadow().reported().build(), false);
            this.reportedTopic.addHandler(this);
        }
    }

    @Override
    public boolean handle(String topicPath, M newShadowReported) throws Exception {
        B3Topic topic = this.topicFactory.parse(topicPath).build();
        return this.handle(topic, newShadowReported);
    }

    private synchronized boolean safeUpdateLastShadowReported(M newShadowReported) {
        if (lastShadowReported == null || lastShadowReported.getVersion() <= newShadowReported.getVersion()) {
            lastShadowReported = newShadowReported;
            return true;
        }
        return false;
    }

    @Override
    public B3Topic.Root getBoundTopicName() {
        return this.topicRoot;
    }

    @Override
    public String getBoundRoleName() {
        return this.roleName;
    }

    @Override
    public void subscribe(B3EventHandler<M> observer) {
        subscribers.add(observer);
    }

    @Override
    public void unsubscribe(B3EventHandler<M> observer) {
        subscribers.remove(observer);
    }

    @Override
    public synchronized void write(M desiredMessage) {
        if (this.desiredTopic == null) {
            log.error("Trying to write before bindTo: message ignored");
            return;
        }
        Integer currentVersion = this.lastShadowReported == null ? Agent.VERSION_WILDCARD
                : this.lastShadowReported.getVersion();
        desiredMessage.setVersion(currentVersion);
        this.desiredTopic.post(desiredMessage);
    }

    @Override
    public boolean handle(B3Topic topic, M newShadowReported) throws Exception {
        if (this.safeUpdateLastShadowReported(newShadowReported)) {
            for (B3EventHandler<M> listener : subscribers) {
                try {
                    listener.handle(topic, newShadowReported);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        return true;
    }

}