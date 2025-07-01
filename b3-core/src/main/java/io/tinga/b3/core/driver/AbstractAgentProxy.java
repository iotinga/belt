package io.tinga.b3.core.driver;

import it.netgrid.bauer.EventHandler;
import it.netgrid.bauer.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.tinga.b3.core.Agent;
import io.tinga.b3.core.AgentProxy;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3Topic;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractAgentProxy<M extends B3Message<?>> implements AgentProxy<M> {
    private static final Logger log = LoggerFactory.getLogger(AbstractAgentProxy.class);

    private B3Topic topicName;
    private String roleName;
    private Topic<M> desiredTopic;
    private Topic<M> reportedTopic;
    private final List<EventHandler<M>> subscribers;
    private final ITopicFactoryProxy topicFactoryProxy;

    private M lastShadowReported;

    public AbstractAgentProxy(ITopicFactoryProxy topicFactoryProxy) {
        this.topicFactoryProxy = topicFactoryProxy;
        this.subscribers = new CopyOnWriteArrayList<>();
        this.reportedTopic.addHandler(this);
    }

    @Override
    public synchronized void bindTo(B3Topic topicName, String roleName) {
        if (desiredTopic == null && this.reportedTopic == null) {
            this.topicName = topicName;
            this.roleName = roleName;
            this.desiredTopic = topicFactoryProxy
                    .getTopic(topicName.shadow().desired(roleName), true);
            this.reportedTopic = topicFactoryProxy.getTopic(topicName.shadow().reported(), false);
            this.reportedTopic.addHandler(this);
        }
    }

    @Override
    public boolean handle(String topic, M newShadowReported) {
        if (this.safeUpdateLastShadowReported(newShadowReported)) {
            for (EventHandler<M> listener : subscribers) {
                try {
                    listener.handle(topic, newShadowReported);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        return true;
    }

    private synchronized boolean safeUpdateLastShadowReported(M newShadowReported) {
        if (lastShadowReported == null || lastShadowReported.getVersion() <= newShadowReported.getVersion()) {
            lastShadowReported = newShadowReported;
            return true;
        }
        return false;
    }

    @Override
    public B3Topic getBoundTopicName() {
        return this.topicName;
    }

    @Override
    public String getBoundRoleName() {
        return this.roleName;
    }

    @Override
    public void subscribe(EventHandler<M> observer) {
        subscribers.add(observer);
    }

    @Override
    public void unsubscribe(EventHandler<M> observer) {
        subscribers.remove(observer);
    }

    @Override
    public synchronized void write(M desiredMessage) {
        if (this.desiredTopic == null) {
            log.error("Trying to write before bindTo: message ignored");
            return;
        }
        Integer currentVersion = this.lastShadowReported == null ? Agent.VERSION_WILDCARD : this.lastShadowReported.getVersion();
        desiredMessage.setVersion(currentVersion);
        this.desiredTopic.post(desiredMessage);
    }

}