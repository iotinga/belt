package io.tinga.b3.entityagent.shadowing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.core.Agent;
import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.protocol.GenericB3Message;
import io.tinga.b3.entityagent.operation.EntityOperationFactory;
import io.tinga.b3.entityagent.operation.EntityOperationGrantsChecker;

public class RoleBasedEdgeFirstDesiredPolicy extends AbstractEntityShadowDesiredPolicy {

    @Inject
    public RoleBasedEdgeFirstDesiredPolicy(EntityOperationGrantsChecker checker,
            EntityOperationFactory operationFactory, VersionSafeExecutor executor,
            EdgeDriver<GenericB3Message> edgeDriver, ITopicFactoryProxy topicFactory) {
        super(checker, operationFactory, executor, edgeDriver, topicFactory);
    }

    private static final Logger log = LoggerFactory.getLogger(RoleBasedEdgeFirstDesiredPolicy.class);

    @Override
    public Class<GenericB3Message> getEventClass() {
        return GenericB3Message.class;
    }

    @Override
    public boolean handle(String topicName, GenericB3Message event) throws Exception {
        this.executor.safeExecute(version -> {
            if (hasNoConflicts(version, event)) {
                log.info(String.format("Refusing desired update: wildcard(%d) desired(%d) current(%d)",
                        Agent.VERSION_WILDCARD, event.getVersion(), version.apply(false)));
                return null;
            }

            processMessage(topicName, event);

            return null;
        });

        return true;
    }
}
