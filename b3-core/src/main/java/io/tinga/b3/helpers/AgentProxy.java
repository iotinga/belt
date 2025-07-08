package io.tinga.b3.helpers;

import io.tinga.b3.agent.Agent;
import io.tinga.b3.protocol.B3EventHandler;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;

/**
 * An Agent Proxy is
 */
public interface AgentProxy<M extends B3Message<?>> extends Agent<M> {


    interface Factory <M extends B3Message<?>> {
        AgentProxy<M> getProxy(B3Topic.Base topicBase, String roleName);
    }

    void write(M desiredMessage);

    void subscribe(B3EventHandler<M> reportedObserver);

    void unsubscribe(B3EventHandler<M> reportedObserver);
}
