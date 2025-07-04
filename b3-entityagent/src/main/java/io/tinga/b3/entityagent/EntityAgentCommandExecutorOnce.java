package io.tinga.b3.entityagent;

import io.tinga.belt.output.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.agent.Agent;
import io.tinga.b3.agent.impl.AbstractAgentCommandExecutor;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor;
import io.tinga.b3.helpers.B3MessageProvider;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.impl.GenericB3Message;

public class EntityAgentCommandExecutorOnce extends AbstractAgentCommandExecutor<GenericB3Message, EntityAgentCommand> {

    private static final Logger log = LoggerFactory.getLogger(EntityAgentCommandExecutorOnce.class);

    @Inject
    private B3MessageProvider<GenericB3Message> provider;

    public EntityAgentCommandExecutorOnce(B3Topic.Root topicRoot, ShadowReportedPolicy<GenericB3Message> reportedPolicy,
            ShadowDesiredPolicy<GenericB3Message> desiredPolicy, VersionSafeExecutor executor,
            Agent.EdgeDriver<GenericB3Message> driver) {
        super(topicRoot, reportedPolicy, desiredPolicy, executor, driver);
    }

    @Override
    public Status execute(EntityAgentCommand command) {
        GenericB3Message message = provider.load(command.desiredRef());
        try {
            B3Topic topic = getBoundTopicName().shadow().desired(command.role()).build();
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
