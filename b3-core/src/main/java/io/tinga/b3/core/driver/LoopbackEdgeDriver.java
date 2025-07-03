package io.tinga.b3.core.driver;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.inject.Inject;

import io.tinga.b3.core.B3EventHandler;
import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.EdgeDriverException;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3Topic;
import io.tinga.b3.protocol.topic.B3TopicRoot;

public class LoopbackEdgeDriver<M extends B3Message<?>> implements EdgeDriver<M> {

    private final List<B3EventHandler<M>> subscribers;
    private final B3TopicRoot topicRoot;
    private final B3Topic shadowReportedTopic;

    @Inject
    public LoopbackEdgeDriver(B3TopicRoot topicRoot) {
        this.subscribers = new CopyOnWriteArrayList<>();
        this.topicRoot = topicRoot;
        this.shadowReportedTopic = this.topicRoot.shadow().reported().build();
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
