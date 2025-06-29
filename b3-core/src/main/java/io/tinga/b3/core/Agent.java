package io.tinga.b3.core;

import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.AgentTopic;
import it.netgrid.bauer.EventHandler;

public interface Agent<M extends B3Message<?>> {
    int VERSION_WILDCARD = 0;

    interface ShadowDesiredPolicy<M> extends EventHandler<M> {
        void bindTo(AgentTopic agent, String roleName);
    }

    interface ShadowReportedPolicy<M> extends EventHandler<M> {
        void bindTo(AgentTopic agent, String roleName);
    }

    interface Config {
        String agentId();
    }

    void bindTo(AgentTopic agent, String roleName);

    AgentTopic getBoundAgentTopic();

    String getBoundRoleName();
}
