package io.tinga.b3.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.belt.output.Status;
import io.tinga.b3.agent.impl.AbstractAgentCommandExecutor;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.protocol.B3Topic;

public class EntityAgentCommandExecutorDaemon extends AbstractAgentCommandExecutor<GenericB3Message, EntityAgentCommand> {

    private static final Logger log = LoggerFactory.getLogger(EntityAgentCommandExecutorDaemon.class);

    @Inject
    public EntityAgentCommandExecutorDaemon(B3Topic.Root topicRoot, ShadowReportedPolicy<GenericB3Message> reportedPolicy,
            ShadowDesiredPolicy<GenericB3Message> desiredPolicy, VersionSafeExecutor executor,
            Agent.EdgeDriver<GenericB3Message> driver) {
        super(topicRoot, reportedPolicy, desiredPolicy, executor, driver);
    }


    @Override
    public Status execute(EntityAgentCommand command) {
        log.info("Executing command... %s", command.toString());
        return Status.OK;
    }

}
