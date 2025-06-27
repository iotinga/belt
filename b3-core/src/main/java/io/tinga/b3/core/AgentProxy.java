package io.tinga.b3.core;

import io.tinga.b3.protocol.RawMessage;
import io.tinga.b3.protocol.topic.AgentTopic;
import it.netgrid.bauer.EventHandler;

/**
 * An Agent Proxy is
 */
public interface AgentProxy<S, M extends RawMessage<S>> extends EventHandler<M>, Agent<S> {
    interface Factory {
        <S, M extends RawMessage<S>> AgentProxy<S, M> getProxy(AgentTopic agent, String roleName);
    }

    void write(M desiredMessage);

    void subscribe(EventHandler<M> reportedObserver);

    void unsubscribe(EventHandler<M> reportedObserver);
}
