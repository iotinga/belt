package io.tinga.b3.helpers;

import com.google.inject.Key;

import io.tinga.b3.protocol.B3EventHandler;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;

/**
 * An Agent Proxy is
 */
public interface AgentProxy<M extends B3Message<?>> {

    interface Factory {
        <M extends B3Message<?>> AgentProxy<M> getProxy(B3Topic.Base topicBase, String roleName, Key<AgentProxy<M>> agentProxyKey);
        AgentProxy<GenericB3Message> getProxy(B3Topic.Base topicBase, String roleName);
    }

    void write(M desiredMessage);

    void subscribe(B3EventHandler<M> reportedObserver);

    void unsubscribe(B3EventHandler<M> reportedObserver);

    void bind(B3Topic.Base topicBase, String roleName);

    B3Topic.Base getBoundTopicBase();

    String getBoundRoleName();
}
