package io.tinga.b3.entityagent;

import io.tinga.belt.output.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.core.impl.AbstractAgentCommandExecutor;
import io.tinga.b3.entityagent.desired.DesiredGenericB3MessageProvider;
import io.tinga.b3.protocol.GenericB3Message;
import io.tinga.b3.protocol.topic.B3Topic;

public class EntityCommandExecutorOnce extends AbstractAgentCommandExecutor<GenericB3Message, EntityCommand> {

    private static final Logger log = LoggerFactory.getLogger(EntityCommandExecutorOnce.class);

    @Inject
    private DesiredGenericB3MessageProvider provider;

    public EntityCommandExecutorOnce(B3Topic topicName, ShadowReportedPolicy<GenericB3Message> reportedPolicy,
            ShadowDesiredPolicy<GenericB3Message> desiredPolicy, VersionSafeExecutor executor,
            EdgeDriver<GenericB3Message> driver) {
        super(topicName, reportedPolicy, desiredPolicy, executor, driver);
    }

    @Override
    public Status execute(EntityCommand command) {
        GenericB3Message message = provider.load(command.desiredRef());
        try {
            String topicName = getBoundTopicName().shadow().desired(command.role()).build();
            this.desiredPolicy.handle(topicName, message);
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
