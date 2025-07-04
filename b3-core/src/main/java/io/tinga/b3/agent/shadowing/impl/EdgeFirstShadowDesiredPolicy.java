package io.tinga.b3.agent.shadowing.impl;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.agent.Agent;
import io.tinga.b3.agent.InvalidOperationException;
import io.tinga.b3.agent.driver.EdgeDriverException;
import io.tinga.b3.agent.shadowing.Operation;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.B3ITopicFactoryProxy;
import it.netgrid.bauer.EventHandler;
import it.netgrid.bauer.Topic;

public class EdgeFirstShadowDesiredPolicy<M extends B3Message<?>>
        implements Agent.ShadowDesiredPolicy<M>, EventHandler<M> {

    private static final Logger log = LoggerFactory.getLogger(EdgeFirstShadowDesiredPolicy.class);

    protected final VersionSafeExecutor executor;
    protected final Agent.EdgeDriver<M> edgeDriver;
    protected final B3ITopicFactoryProxy topicFactoryProxy;
    protected final Operation.Factory operationFactory;
    protected final Operation.GrantsChecker<M> grantsChecker;
    protected final Class<M> messageClass;
    protected final B3Topic.Factory topicFactory;

    protected Topic<M> topic;

    @Inject
    public EdgeFirstShadowDesiredPolicy(Class<M> messageClass, VersionSafeExecutor executor, Agent.EdgeDriver<M> edgeDriver,
            B3ITopicFactoryProxy topicFactoryProxy, Operation.Factory operationFactory, B3Topic.Factory topicFactory,
            Operation.GrantsChecker<M> grantsChecker) {
        this.messageClass = messageClass;
        this.executor = executor;
        this.edgeDriver = edgeDriver;
        this.topicFactoryProxy = topicFactoryProxy;
        this.operationFactory = operationFactory;
        this.grantsChecker = grantsChecker;
        this.topicFactory = topicFactory;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean handle(B3Topic topic, M event) throws Exception {
        this.executor.safeExecute(version -> {
            if (hasConflicts(version, event)) {
                log.info(String.format("Refusing desired update: wildcard(%d) desired(%d) current(%d)",
                        Agent.VERSION_WILDCARD, event.getVersion(), version.apply(false)));
                return null;
            }

            try {
                Operation<M> operation = operationFactory.buildFrom(topic, event);
                if (grantsChecker.isAllowed(operation)) {
                    this.edgeDriver.write(event);
                }
            } catch (EdgeDriverException | InvalidOperationException e) {
                log.warn(e.getMessage());
            }

            return null;
        });

        return true;
    }

    public boolean hasConflicts(Function<Boolean, Integer> version, M event) {
        return !event.getVersion().equals(Agent.VERSION_WILDCARD) && !version.apply(false).equals(event.getVersion());
    }

    @Override
    public void bindTo(B3Topic.Root topicRoot, String roleName) {
        this.topic = this.topicFactoryProxy.getTopic(topicRoot.shadow().desired("#").build(), false);
        this.topic.addHandler(this);
    }

    @Override
    public Class<M> getEventClass() {
        return messageClass;
    }

    @Override
    public boolean handle(String topicPath, M event) throws Exception {
        B3Topic topic = topicFactory.parse(topicPath).build();
        return this.handle(topic, event);
    }

}
