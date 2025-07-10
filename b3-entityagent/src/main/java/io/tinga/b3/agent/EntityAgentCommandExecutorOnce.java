package io.tinga.b3.agent;

import io.tinga.belt.output.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.agent.security.Operation.GrantsChecker;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor;
import io.tinga.b3.helpers.AgentProxy;
import io.tinga.b3.helpers.B3MessageProvider;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.B3Topic.Base;

public class EntityAgentCommandExecutorOnce extends AbstractAgentCommandExecutor<GenericB3Message, EntityAgentCommand> {

    private static final Logger log = LoggerFactory.getLogger(EntityAgentCommandExecutorOnce.class);

    private final B3MessageProvider<GenericB3Message> desiredMessageProvider;

    @Inject
    public EntityAgentCommandExecutorOnce(
            B3MessageProvider<GenericB3Message> desiredMessageProvider,
            AgentProxy<GenericB3Message> agentProxy, Base topicBase,
            ShadowReportedPolicy<GenericB3Message> reportedPolicy, ShadowDesiredPolicy<GenericB3Message> desiredPolicy,
            VersionSafeExecutor executor, GrantsChecker<GenericB3Message> grantsChecker,
            EdgeDriver<GenericB3Message> driver) {
        super(agentProxy, topicBase, reportedPolicy, desiredPolicy, executor, grantsChecker, driver);
        this.desiredMessageProvider = desiredMessageProvider;
    }

    @Override
    public Status execute(EntityAgentCommand command) {
        GenericB3Message message = desiredMessageProvider.load(command.desiredRef());
        try {
            B3Topic topic = getBoundTopicBase().shadow().desired(command.role()).build();
            this.desiredPolicy.handle(topic, message);
            return Status.OK;
        } catch (Exception e) {
            log.error("Unexpected error occurred during message processing", e);
            return Status.BAD_REQUEST;
        }
    }

    @Override
    protected boolean keepAlive() {
        return false;
    }

}
