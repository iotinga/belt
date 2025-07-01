package io.tinga.b3.entityagent.shadowing;

import com.google.inject.Inject;

import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.protocol.GenericB3Message;
import io.tinga.b3.entityagent.operation.EntityOperationFactory;
import io.tinga.b3.entityagent.operation.EntityOperationGrantsChecker;

public class RoleBasedPassthroughDesiredPolicy extends AbstractEntityShadowDesiredPolicy {

    @Inject
    public RoleBasedPassthroughDesiredPolicy(EntityOperationGrantsChecker checker,
            EntityOperationFactory operationFactory, VersionSafeExecutor executor,
            EdgeDriver<GenericB3Message> edgeDriver, ITopicFactoryProxy topicFactory) {
        super(checker, operationFactory, executor, edgeDriver, topicFactory);
    }

    @Override
    public boolean handle(String topicName, GenericB3Message event) throws Exception {
        this.executor.safeExecute(version -> {
            processMessage(topicName, event);
            return null;
        });

        return true;
    }

}
