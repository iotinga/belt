package io.tinga.b3.groupagent;

import com.google.inject.Inject;

import io.tinga.b3.agent.Agent;
import io.tinga.b3.agent.impl.AbstractAgentCommandExecutor;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.impl.GenericB3Message;
import io.tinga.belt.output.Status;

public class GroupAgentCommandExecutor extends AbstractAgentCommandExecutor<GenericB3Message, GroupAgentCommand> {

    @Inject
    public GroupAgentCommandExecutor(B3Topic.Root topicRoot, ShadowReportedPolicy<GenericB3Message> reportedPolicy,
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
