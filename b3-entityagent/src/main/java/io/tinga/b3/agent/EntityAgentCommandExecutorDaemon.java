package io.tinga.b3.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.belt.output.Status;
import io.tinga.b3.agent.security.Operation.GrantsChecker;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.helpers.AgentProxy.Factory;
import io.tinga.b3.protocol.B3Topic.Base;

public class EntityAgentCommandExecutorDaemon extends AbstractAgentCommandExecutor<GenericB3Message, EntityAgentCommand> {
    private static final Logger log = LoggerFactory.getLogger(EntityAgentCommandExecutorDaemon.class);

    @Inject
    public EntityAgentCommandExecutorDaemon(Factory<GenericB3Message> agentProxyFactory, Base topicBase,
            ShadowReportedPolicy<GenericB3Message> reportedPolicy, ShadowDesiredPolicy<GenericB3Message> desiredPolicy,
            VersionSafeExecutor executor, GrantsChecker<GenericB3Message> grantsChecker,
            EdgeDriver<GenericB3Message> driver) {
        super(agentProxyFactory, topicBase, reportedPolicy, desiredPolicy, executor, grantsChecker, driver);
    }

    @Override
    public Status execute(EntityAgentCommand command) {
        log.info("Executing command... %s", command.toString());
        return Status.OK;
    }

}
