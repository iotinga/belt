package io.tinga.b3.groupagent;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.core.impl.AbstractAgentCommandExecutor;
import io.tinga.b3.protocol.GenericMessage;
import io.tinga.b3.protocol.topic.AgentTopic;
import io.tinga.belt.output.Status;

public class GroupAgentActionExecutor extends AbstractAgentCommandExecutor<GroupAgentCommand> {

    @Inject
    public GroupAgentActionExecutor(AgentTopic agentTopic,
            ShadowReportedPolicy<JsonNode, GenericMessage> reportedPolicy,
            ShadowDesiredPolicy<JsonNode, GenericMessage> desiredPolicy, VersionSafeExecutor executor,
            EdgeDriver<JsonNode, GenericMessage> driver) {
        super(agentTopic, reportedPolicy, desiredPolicy, executor, driver);
    }

    @Override
    public Status execute(GroupAgentCommand command) {
        // NOTHING TO DO
        return Status.OK;
    }

}
