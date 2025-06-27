package io.tinga.b3.core;

import io.tinga.b3.protocol.RawMessage;
import io.tinga.b3.protocol.topic.AgentTopic;
import it.netgrid.bauer.EventHandler;

public interface Agent<S> {
    int VERSION_WILDCARD = 0;

    interface ShadowDesiredPolicy<S, M extends RawMessage<S>> extends EventHandler<M> {
        void bindTo(AgentTopic agent, String roleName);
    }

    interface ShadowReportedPolicy<S, M extends RawMessage<S>> extends EventHandler<M> {
        void bindTo(AgentTopic agent, String roleName);
    }

    interface Command {
        String agentId();
    }

    void bindTo(AgentTopic agent, String roleName);

    AgentTopic getBoundAgentTopic();

    String getBoundRoleName();
}
