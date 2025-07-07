package io.tinga.b3.agent.driver.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.tinga.b3.agent.driver.AgentProxy;
import io.tinga.b3.helpers.B3MessageProvider;
import io.tinga.b3.protocol.B3EventHandler;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.B3Topic.Base;

public class ReadOnlyMessageProviderAgentProxy<M extends B3Message<?>> implements AgentProxy<M> {

    private static final Logger log = LoggerFactory.getLogger(ReadOnlyMessageProviderAgentProxy.class);

    private B3Topic.Base topicBase;
    private String roleName;
    private final List<B3EventHandler<M>> subscribers;
    private final B3MessageProvider<M> provider;

    public ReadOnlyMessageProviderAgentProxy(B3MessageProvider<M> provider) {
        this.subscribers = new CopyOnWriteArrayList<>();
        this.provider = provider;
    }

    @Override
    public void bind(Base topicBase, String roleName) {
        this.topicBase = topicBase;
        this.roleName = roleName;
        B3Topic topic = topicBase.shadow().reported().build();
        M message = this.provider.load(topic.toString());
        for (B3EventHandler<M> listener : subscribers) {
            try {
                listener.handle(topic, message);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public void write(M desiredMessage) {
        throw new UnsupportedOperationException("Unimplemented method 'write'");
    }

    @Override
    public B3Topic.Base getBoundTopicBase() {
        return this.topicBase;
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

}
