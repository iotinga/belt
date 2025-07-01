package io.tinga.b3.entityagent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.belt.output.Status;
import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.core.impl.AbstractAgentCommandExecutor;
import io.tinga.b3.entityagent.operation.EntityMessage;
import io.tinga.b3.protocol.topic.B3Topic;

public class EntityCommandExecutorDaemon extends AbstractAgentCommandExecutor<EntityMessage, EntityCommand> {

    private static final Logger log = LoggerFactory.getLogger(EntityCommandExecutorDaemon.class);

    @Inject
    public EntityCommandExecutorDaemon(B3Topic topicName, ShadowReportedPolicy<EntityMessage> reportedPolicy,
            ShadowDesiredPolicy<EntityMessage> desiredPolicy, VersionSafeExecutor executor,
            EdgeDriver<EntityMessage> driver) {
        super(topicName, reportedPolicy, desiredPolicy, executor, driver);
    }


    @Override
    public Status execute(EntityCommand command) {
        log.info("Executing command... %s", command.toString());
        return Status.OK;
    }

}
