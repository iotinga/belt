package io.tinga.b3.core.shadowing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.core.Agent;
import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.EdgeDriverException;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.core.shadowing.operation.InvalidOperationException;
import io.tinga.b3.core.shadowing.operation.Operation;
import io.tinga.b3.core.shadowing.operation.OperationFactory;
import io.tinga.b3.core.shadowing.operation.OperationGrantsChecker;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3TopicRoot;
import io.tinga.belt.helpers.AEventHandler;

public class PassthroughShadowDesiredPolicy<M extends B3Message<?>> extends AEventHandler<M>
        implements Agent.ShadowDesiredPolicy<M> {
    private static final Logger log = LoggerFactory.getLogger(PassthroughShadowDesiredPolicy.class);

    protected final VersionSafeExecutor executor;
    protected final EdgeDriver<M> edgeDriver;
    protected final ITopicFactoryProxy topicFactory;
    protected final OperationFactory operationFactory;
    protected final OperationGrantsChecker<M> grantsChecker;

    @Inject
    public PassthroughShadowDesiredPolicy(Class<M> eventClass, VersionSafeExecutor executor, EdgeDriver<M> edgeDriver,
            ITopicFactoryProxy topicFactory, OperationFactory operationFactory,
            OperationGrantsChecker<M> grantsChecker) {
        super(eventClass);
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
    public boolean handle(String topicRoot, M event) throws Exception {
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
