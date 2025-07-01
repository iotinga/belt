package io.tinga.b3.entityagent.shadowing;

import com.google.inject.Inject;

import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.protocol.GenericB3Message;
import io.tinga.b3.entityagent.operation.OperationFactory;
import io.tinga.b3.entityagent.operation.OperationGrantsChecker;

public class RoleBasedPassthroughDesiredPolicy extends AbstractEntityAgentShadowDesiredPolicy {

    @Inject
    public RoleBasedPassthroughDesiredPolicy(OperationGrantsChecker checker,
            OperationFactory operationFactory, VersionSafeExecutor executor,
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
