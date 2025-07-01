package io.tinga.b3.entityagent;

import io.tinga.belt.output.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.core.impl.AbstractAgentCommandExecutor;
import io.tinga.b3.entityagent.desired.DesiredEntityMessageProvider;
import io.tinga.b3.entityagent.operation.EntityMessage;
import io.tinga.b3.protocol.topic.AgentTopic;

public class EntityCommandExecutorOnce extends AbstractAgentCommandExecutor<EntityMessage, EntityCommand> {

    private static final Logger log = LoggerFactory.getLogger(EntityCommandExecutorOnce.class);

    @Inject
    private DesiredEntityMessageProvider provider;

    public EntityCommandExecutorOnce(AgentTopic agentTopic, ShadowReportedPolicy<EntityMessage> reportedPolicy,
            ShadowDesiredPolicy<EntityMessage> desiredPolicy, VersionSafeExecutor executor,
            EdgeDriver<EntityMessage> driver) {
        super(agentTopic, reportedPolicy, desiredPolicy, executor, driver);
    }

    @Override
    public Status execute(EntityCommand command) {
        EntityMessage message = provider.load(command.desiredRef());
        try {
            this.desiredPolicy.handle(command.topic(), message);
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
