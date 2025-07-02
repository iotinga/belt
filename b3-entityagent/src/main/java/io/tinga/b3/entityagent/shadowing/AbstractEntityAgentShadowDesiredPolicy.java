package io.tinga.b3.entityagent.shadowing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.EdgeDriverException;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.core.shadowing.EdgeFirstShadowDesiredPolicy;
import io.tinga.b3.protocol.GenericB3Message;
import io.tinga.b3.entityagent.operation.Operation;
import io.tinga.b3.entityagent.operation.OperationFactory;
import io.tinga.b3.entityagent.operation.OperationGrantsChecker;
import io.tinga.b3.entityagent.operation.InvalidOperationException;

public abstract class AbstractEntityAgentShadowDesiredPolicy extends EdgeFirstShadowDesiredPolicy<GenericB3Message> {

    private static final Logger log = LoggerFactory.getLogger(RoleBasedEdgeFirstDesiredPolicy.class);

    private final OperationGrantsChecker checker;
    private final OperationFactory operationFactory;

    @Inject
    public AbstractEntityAgentShadowDesiredPolicy(OperationGrantsChecker checker,
            OperationFactory operationFactory, VersionSafeExecutor executor,
            EdgeDriver<GenericB3Message> edgeDriver,
            ITopicFactoryProxy topicFactory) {
        super(GenericB3Message.class, executor, edgeDriver, topicFactory);
        this.checker = checker;
        this.operationFactory = operationFactory;
    }

    @Override
    abstract public boolean handle(String topicName, GenericB3Message event) throws Exception;

    protected boolean processMessage(String topicName, GenericB3Message event) {
        try {
            Operation operation = operationFactory.buildFrom(topicName, event);
            boolean result = checker.isAllowed(operation);
            if (result) {
                log.info("[ ALLOW]: %s %s@%s -> %s", operation.message().getAction().name(), operation.role(),
                        operation.desiredTopic(), operation.reportedTopic());
                this.edgeDriver.write(event);
                return true;
            } else {
                log.info("[REJECT]: %s %s@%s -> %s", operation.message().getAction().name(), operation.role(),
                        operation.desiredTopic(), operation.reportedTopic());
                return false;
            }
        } catch (EdgeDriverException e) {
            log.warn(e.getMessage());
                return false;
        } catch (InvalidOperationException e) {
            log.warn(e.getMessage());
                return false;
        }
    }
}
