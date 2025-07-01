package io.tinga.b3.entityagent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.belt.output.Status;
import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.core.impl.AbstractAgentCommandExecutor;
import io.tinga.b3.protocol.GenericB3Message;
import io.tinga.b3.protocol.topic.B3Topic;

public class EntityCommandExecutorDaemon extends AbstractAgentCommandExecutor<GenericB3Message, EntityCommand> {

    private static final Logger log = LoggerFactory.getLogger(EntityCommandExecutorDaemon.class);

    @Inject
    public EntityCommandExecutorDaemon(B3Topic topicName, ShadowReportedPolicy<GenericB3Message> reportedPolicy,
            ShadowDesiredPolicy<GenericB3Message> desiredPolicy, VersionSafeExecutor executor,
            EdgeDriver<GenericB3Message> driver) {
        super(topicName, reportedPolicy, desiredPolicy, executor, driver);
    }


    @Override
    public Status execute(EntityCommand command) {
        log.info("Executing command... %s", command.toString());
        return Status.OK;
    }

}
