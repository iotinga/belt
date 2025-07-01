package io.tinga.b3.groupagent;

import com.google.inject.Inject;

import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.core.impl.AbstractAgentCommandExecutor;
import io.tinga.b3.protocol.GenericB3Message;
import io.tinga.b3.protocol.topic.B3Topic;
import io.tinga.belt.output.Status;

public class GroupAgentCommandExecutor extends AbstractAgentCommandExecutor<GenericB3Message, GroupAgentCommand> {

    @Inject
    public GroupAgentCommandExecutor(B3Topic topicName, ShadowReportedPolicy<GenericB3Message> reportedPolicy,
            ShadowDesiredPolicy<GenericB3Message> desiredPolicy, VersionSafeExecutor executor,
            EdgeDriver<GenericB3Message> driver) {
        super(agentTopic, reportedPolicy, desiredPolicy, executor, driver);
    }

    @Override
    public Status execute(GroupAgentCommand command) {
        // NOTHING TO DO
        return Status.OK;
    }

}
