package io.tinga.b3.entityagent.shadowing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.core.Agent;
import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.EdgeDriverException;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.core.shadowing.AbstractEdgeFirstShadowDesiredPolicy;
import io.tinga.b3.entityagent.operation.EntityMessage;
import io.tinga.b3.entityagent.operation.EntityOperation;
import io.tinga.b3.entityagent.operation.EntityOperationFactory;
import io.tinga.b3.entityagent.operation.EntityOperationGrantsChecker;
import io.tinga.b3.entityagent.operation.InvalidEntityOperationException;

public class RoleBasedEdgeFirstDesiredPolicy extends AbstractEdgeFirstShadowDesiredPolicy<EntityMessage> {

    private static final Logger log = LoggerFactory.getLogger(RoleBasedEdgeFirstDesiredPolicy.class);

    private final EntityOperationGrantsChecker checker;
    private final EntityOperationFactory operationFactory;

    @Inject
    public RoleBasedEdgeFirstDesiredPolicy(EntityOperationGrantsChecker checker,
            EntityOperationFactory operationFactory, VersionSafeExecutor executor,
            EdgeDriver<EntityMessage> fieldDriver,
            ITopicFactoryProxy topicFactory) {
        super(executor, fieldDriver, topicFactory);
        this.checker = checker;
        this.operationFactory = operationFactory;
    }

    @Override
    public Class<EntityMessage> getEventClass() {
        return EntityMessage.class;
    }

    @Override
    public boolean handle(String topicName, EntityMessage event) throws Exception {
        this.executor.safeExecute(version -> {
            if (hasNoConflicts(version, event)) {
                log.info(String.format("Refusing desired update: wildcard(%d) desired(%d) current(%d)",
                        Agent.VERSION_WILDCARD, event.getVersion(), version.apply(false)));
                return null;
            }

            try {
                EntityOperation operation = operationFactory.buildFrom(topicName, event);
                boolean result = checker.isAllowed(operation);
                if (result) {
                    log.info("[ ALLOW]: %s %s@%s -> %s", operation.message().getAction().name(), operation.role(),
                            operation.desiredTopic(), operation.reportedTopic());
                    this.fieldDriver.write(event);
                } else {
                    log.info("[REJECT]: %s %s@%s -> %s", operation.message().getAction().name(), operation.role(),
                            operation.desiredTopic(), operation.reportedTopic());
                }
            } catch (EdgeDriverException e) {
                log.warn(e.getMessage());
            } catch (InvalidEntityOperationException e) {
                log.warn(e.getMessage());
            }

            return null;
        });

        return true;
    }
}
