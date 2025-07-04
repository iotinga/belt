package io.tinga.b3.agent.driver.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.inject.Inject;

import io.tinga.b3.agent.Agent;
import io.tinga.b3.agent.driver.ConnectionState;
import io.tinga.b3.agent.driver.EdgeDriverException;
import io.tinga.b3.protocol.B3EventHandler;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;

public class LoopbackEdgeDriver<M extends B3Message<?>> implements Agent.EdgeDriver<M> {

    private final List<B3EventHandler<M>> subscribers;
    private final B3Topic.Base topicBase;
    private final B3Topic shadowReportedTopic;

    @Inject
    public LoopbackEdgeDriver(B3Topic.Base topicBase) {
        this.subscribers = new CopyOnWriteArrayList<>();
        this.topicBase = topicBase;
        this.shadowReportedTopic = this.topicBase.shadow().reported().build();
    }

    @Override
    public void connect() {
        // NOTHING TO DO
    }

    @Override
    public void disconnect() {
        // NOTHING TO DO
    }

    @Override
    public ConnectionState getConnectionState() {
        return ConnectionState.CONNECTED;
    }

    @Override
    public void write(M desiredMessage) throws EdgeDriverException {
        if (desiredMessage == null) {
            throw new EdgeDriverException("Invalid shadow desired message: desiredMessage is null");
        }

        for (B3EventHandler<M> subscriber : this.subscribers) {
            try {
                subscriber.handle(this.shadowReportedTopic, desiredMessage);
            } catch (Exception e) {
                throw new EdgeDriverException(String.format("Unexpected error occurred sending message to %s: %s", subscriber.getName(),
                        e.getMessage()), e);
            }
        }
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
