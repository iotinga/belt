package io.tinga.b3.entityagent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.belt.output.Status;
import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.core.agent.AbstractAgentCommandExecutor;
import io.tinga.b3.protocol.GenericB3Message;
import io.tinga.b3.protocol.topic.B3Topic;

public class EntityAgentCommandExecutorDaemon extends AbstractAgentCommandExecutor<GenericB3Message, EntityAgentCommand> {

    private static final Logger log = LoggerFactory.getLogger(EntityAgentCommandExecutorDaemon.class);

    @Inject
    public EntityAgentCommandExecutorDaemon(B3Topic topicRoot, ShadowReportedPolicy<GenericB3Message> reportedPolicy,
            ShadowDesiredPolicy<GenericB3Message> desiredPolicy, VersionSafeExecutor executor,
            EdgeDriver<GenericB3Message> driver) {
        super(topicRoot, reportedPolicy, desiredPolicy, executor, driver);
    }


    @Override
    public Status execute(EntityAgentCommand command) {
        log.info("Executing command... %s", command.toString());
        return Status.OK;
    }

}
