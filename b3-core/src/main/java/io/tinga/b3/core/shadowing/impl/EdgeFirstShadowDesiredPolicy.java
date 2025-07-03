package io.tinga.b3.core.shadowing.impl;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.core.Agent;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.core.InvalidOperationException;
import io.tinga.b3.core.Operation;
import io.tinga.b3.core.OperationFactory;
import io.tinga.b3.core.OperationGrantsChecker;
import io.tinga.b3.core.driver.EdgeDriver;
import io.tinga.b3.core.driver.EdgeDriverException;
import io.tinga.b3.core.shadowing.VersionSafeExecutor;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3Topic;
import io.tinga.b3.protocol.topic.B3TopicFactory;
import io.tinga.b3.protocol.topic.B3TopicRoot;
import it.netgrid.bauer.EventHandler;
import it.netgrid.bauer.Topic;

public class EdgeFirstShadowDesiredPolicy<M extends B3Message<?>>
        implements Agent.ShadowDesiredPolicy<M>, EventHandler<M> {

    private static final Logger log = LoggerFactory.getLogger(EdgeFirstShadowDesiredPolicy.class);

    protected final VersionSafeExecutor executor;
    protected final EdgeDriver<M> edgeDriver;
    protected final ITopicFactoryProxy topicFactoryProxy;
    protected final OperationFactory operationFactory;
    protected final OperationGrantsChecker<M> grantsChecker;
    protected final Class<M> messageClass;
    protected final B3TopicFactory topicFactory;

    protected Topic<M> topic;

    @Inject
    public EdgeFirstShadowDesiredPolicy(Class<M> messageClass, VersionSafeExecutor executor, EdgeDriver<M> edgeDriver,
            ITopicFactoryProxy topicFactoryProxy, OperationFactory operationFactory, B3TopicFactory topicFactory,
            OperationGrantsChecker<M> grantsChecker) {
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
    public void bindTo(B3TopicRoot topicRoot, String roleName) {
        this.topic = this.topicFactoryProxy.getTopic(topicRoot.shadow().desired("#"), false);
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
