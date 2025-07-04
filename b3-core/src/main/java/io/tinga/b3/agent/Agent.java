package io.tinga.b3.agent;

import io.tinga.b3.agent.driver.ConnectionState;
import io.tinga.b3.agent.driver.EdgeDriverException;
import io.tinga.b3.protocol.B3EventHandler;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;

public interface Agent<M extends B3Message<?>> {
    int VERSION_WILDCARD = 0;

    interface ShadowDesiredPolicy<M extends B3Message<?>> extends B3EventHandler<M> {
        void bindTo(B3Topic.Root topicRoot, String roleName);
    }

    interface ShadowReportedPolicy<M extends B3Message<?>> extends B3EventHandler<M> {
        void bindTo(B3Topic.Root topicRoot, String roleName);
    }

    interface Config {
        String agentId();
    }

    public interface EdgeDriver<M extends B3Message<?>> {

        ConnectionState getConnectionState();

        void connect();

        void disconnect();

        void write(M desiredMessage) throws EdgeDriverException;

        void subscribe(B3EventHandler<M> reportedObserver);

        void unsubscribe(B3EventHandler<M> reportedObserver);

    }

    interface LocalShadowingConfig {
        String getReportedStoreRef();
    }

    void bindTo(B3Topic.Root topicRoot, String roleName);

    B3Topic.Root getBoundTopicName();

    String getBoundRoleName();
}
