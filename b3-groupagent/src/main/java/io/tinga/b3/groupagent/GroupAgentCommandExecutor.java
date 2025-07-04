package io.tinga.b3.groupagent;

import com.google.inject.Inject;

import io.tinga.b3.core.Agent;
import io.tinga.b3.core.impl.AbstractAgentCommandExecutor;
import io.tinga.b3.core.shadowing.VersionSafeExecutor;
import io.tinga.b3.protocol.GenericB3Message;
import io.tinga.b3.protocol.topic.B3TopicRoot;
import io.tinga.belt.output.Status;

public class GroupAgentCommandExecutor extends AbstractAgentCommandExecutor<GenericB3Message, GroupAgentCommand> {

    @Inject
    public GroupAgentCommandExecutor(B3TopicRoot topicRoot, ShadowReportedPolicy<GenericB3Message> reportedPolicy,
            ShadowDesiredPolicy<GenericB3Message> desiredPolicy, VersionSafeExecutor executor,
            Agent.EdgeDriver<GenericB3Message> driver) {
        super(topicRoot, reportedPolicy, desiredPolicy, executor, driver);
    }

    @Override
    public Status execute(GroupAgentCommand command) {
        // NOTHING TO DO
        return Status.OK;
    }

}
