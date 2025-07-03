package io.tinga.b3.core;

import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3TopicRoot;

public interface Agent<M extends B3Message<?>> {
    int VERSION_WILDCARD = 0;

    interface ShadowDesiredPolicy<M> extends B3EventHandler<M> {
        void bindTo(B3TopicRoot topicRoot, String roleName);
    }

    interface ShadowReportedPolicy<M> extends B3EventHandler<M> {
        void bindTo(B3TopicRoot topicRoot, String roleName);
    }

    interface Config {
        String agentId();
    }

    interface LocalShadowingConfig {
        String getReportedStoreRef();
    }

    void bindTo(B3TopicRoot topicRoot, String roleName);

    B3TopicRoot getBoundTopicName();

    String getBoundRoleName();
}
