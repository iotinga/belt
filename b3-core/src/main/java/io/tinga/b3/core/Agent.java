package io.tinga.b3.core;

import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3Topic;
import it.netgrid.bauer.EventHandler;

public interface Agent<M extends B3Message<?>> {
    int VERSION_WILDCARD = 0;

    interface ShadowDesiredPolicy<M> extends EventHandler<M> {
        void bindTo(B3Topic topicRoot, String roleName);
    }

    interface ShadowReportedPolicy<M> extends EventHandler<M> {
        void bindTo(B3Topic topicRoot, String roleName);
    }

    interface Config {
        String agentId();
    }

    interface LocalShadowingConfig {
        String getReportedStoreRef();
    }

    void bindTo(B3Topic topicRoot, String roleName);

    B3Topic getBoundTopicName();

    String getBoundRoleName();
}
