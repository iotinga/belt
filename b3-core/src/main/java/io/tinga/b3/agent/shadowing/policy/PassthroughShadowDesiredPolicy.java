package io.tinga.b3.agent.shadowing.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.agent.Agent;
import io.tinga.b3.agent.InvalidOperationException;
import io.tinga.b3.agent.driver.EdgeDriverException;
import io.tinga.b3.agent.security.Operation;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.B3ITopicFactoryProxy;

public class PassthroughShadowDesiredPolicy<M extends B3Message<?>>
        implements Agent.ShadowDesiredPolicy<M> {
    private static final Logger log = LoggerFactory.getLogger(PassthroughShadowDesiredPolicy.class);

    protected final VersionSafeExecutor executor;
    protected final Agent.EdgeDriver<M> edgeDriver;
    protected final B3ITopicFactoryProxy topicFactory;
    protected final Operation.Factory operationFactory;
    protected final Operation.GrantsChecker<M> grantsChecker;

    @Inject
    public PassthroughShadowDesiredPolicy(VersionSafeExecutor executor, Agent.EdgeDriver<M> edgeDriver,
            B3ITopicFactoryProxy topicFactory, Operation.Factory operationFactory,
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
    public boolean handle(B3Topic topicBase, M event) throws Exception {
        this.executor.safeExecute(version -> {
            try {
                Operation<M> operation = operationFactory.buildFrom(topicBase, event);
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
    public void bind(B3Topic.Base topicBase, String roleName) {
        // NOTHING TO DO
    }

}
