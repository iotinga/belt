package io.tinga.b3.core.shadowing.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.core.Agent;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.core.InvalidOperationException;
import io.tinga.b3.core.driver.EdgeDriverException;
import io.tinga.b3.core.shadowing.Operation;
import io.tinga.b3.core.shadowing.VersionSafeExecutor;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3Topic;
import io.tinga.b3.protocol.topic.B3TopicRoot;

public class PassthroughShadowDesiredPolicy<M extends B3Message<?>>
        implements Agent.ShadowDesiredPolicy<M> {
    private static final Logger log = LoggerFactory.getLogger(PassthroughShadowDesiredPolicy.class);

    protected final VersionSafeExecutor executor;
    protected final Agent.EdgeDriver<M> edgeDriver;
    protected final ITopicFactoryProxy topicFactory;
    protected final Operation.Factory operationFactory;
    protected final Operation.GrantsChecker<M> grantsChecker;

    @Inject
    public PassthroughShadowDesiredPolicy(VersionSafeExecutor executor, Agent.EdgeDriver<M> edgeDriver,
            ITopicFactoryProxy topicFactory, Operation.Factory operationFactory,
            Operation.GrantsChecker<M> grantsChecker) {
        this.executor = executor;
        this.edgeDriver = edgeDriver;
        this.topicFactory = topicFactory;
        this.operationFactory = operationFactory;
        this.grantsChecker = grantsChecker;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean handle(B3Topic topicRoot, M event) throws Exception {
        this.executor.safeExecute(version -> {
            try {
                Operation<M> operation = operationFactory.buildFrom(topicRoot, event);
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

    @Override
    public void bindTo(B3TopicRoot topicRoot, String roleName) {
        // NOTHING TO DO
    }

}
