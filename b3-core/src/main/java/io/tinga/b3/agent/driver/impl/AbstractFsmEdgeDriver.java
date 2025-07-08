package io.tinga.b3.agent.driver.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.tinga.b3.agent.Agent;
import io.tinga.b3.agent.driver.ConnectionState;
import io.tinga.b3.agent.driver.EdgeDriverException;
import io.tinga.b3.protocol.B3EventHandler;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;

public abstract class AbstractFsmEdgeDriver<E, M extends B3Message<?>>
        implements Agent.EdgeDriver<M> {

    private static final Logger log = LoggerFactory.getLogger(AbstractFsmEdgeDriver.class);

    public record Context<M>(M incomingDesired, Function<M, Void> reportedEmitter) {
    };
    public interface State<E, M extends B3Message<?>> {

        void enter(Context<M> context);

        E current();

        E onConnectDelta(Context<M> context);

        E onDisconnectDelta(Context<M> context);

        E onWriteDelta(Context<M> context);

        E onEmitDelta(Context<M> context);

        ConnectionState getConnectionState();

        void exit(Context<M> context);

    }

    protected final List<B3EventHandler<M>> subscribers;
    protected final B3Topic.Base topicBase;
    protected final B3Topic shadowReportedTopic;

    private Context<M> currentContext;
    private State<E, M> state;

    public AbstractFsmEdgeDriver(B3Topic.Base topicBase, State<E, M> initialState) {
        this.subscribers = new CopyOnWriteArrayList<>();
        this.topicBase = topicBase;
        this.shadowReportedTopic = this.topicBase.shadow().reported().build();
        this.state = initialState;
        this.currentContext = new Context<M>(null, this::emit);
        this.state.enter(this.currentContext);
    }

    protected abstract State<E, M> get(E state);

    /**
     * Edge Driver Interface ------------------------------------
     */
    @Override
    public void connect() {
        this.onEvent(this.state::onConnectDelta, null, null);
    }

    @Override
    public void disconnect() {
        this.onEvent(this.state::onDisconnectDelta, null, null);
    }

    @Override
    public void write(M desiredMessage) throws EdgeDriverException {
        if (desiredMessage == null) {
            throw new EdgeDriverException("Invalid shadow desired message: desiredMessage is null");
        }

        this.onEvent(this.state::onWriteDelta, desiredMessage, null);
    }

    @Override
    public void subscribe(B3EventHandler<M> observer) {
        subscribers.add(observer);
    }

    @Override
    public void unsubscribe(B3EventHandler<M> observer) {
        subscribers.remove(observer);
    }

    @Override
    public ConnectionState getConnectionState() {
        return this.state.getConnectionState();
    }

    private Void emit(M reportedMessage) {
        this.onEvent(this.state::onEmitDelta, null, reportedMessage);
        return null;
    }

    protected E getCurrentState() {
        return this.state.current();
    }

    protected final synchronized void onEvent(Function<Context<M>, E> delta, M desired, M reported) {

        if(reported != null) {
            for (B3EventHandler<M> subscriber : this.subscribers) {
                try {
                    subscriber.handle(this.shadowReportedTopic, reported);
                } catch (Exception e) {
                    log.error(String.format("Unexpected error occurred sending message to %s: %s", subscriber.getName(),
                            e.getMessage()), e);
                }
            }
        }

        this.currentContext = new Context<M>(desired, this::emit);
        E next = delta.apply(this.currentContext);
        
        if (this.state.current() != next) {
            this.currentContext = new Context<M>(null, this::emit);
            this.state.exit(this.currentContext);
            this.state = this.get(next);
            this.state.enter(this.currentContext);
        }
    }

}
