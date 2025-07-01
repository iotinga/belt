package io.tinga.b3.core;

import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3Topic;
import it.netgrid.bauer.EventHandler;

/**
 * An Agent Proxy is
 */
public interface AgentProxy<M extends B3Message<?>> extends EventHandler<M>, Agent<M> {
    interface Factory {
        <M extends B3Message<?>> AgentProxy<M> getProxy(B3Topic topicName, String roleName);
    }

    void write(M desiredMessage);

    void subscribe(EventHandler<M> reportedObserver);

    void unsubscribe(EventHandler<M> reportedObserver);
}
