package io.tinga.b3.groupagent;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.core.impl.AgentCommandExecutor;
import io.tinga.b3.protocol.GenericMessage;
import io.tinga.b3.protocol.topic.RootTopic;

public class GroupAgentCommandExecutor extends AgentCommandExecutor<GroupAgentCommand> {

    @Inject
    public GroupAgentCommandExecutor(RootTopic rootTopic,
            ShadowReportedPolicy<JsonNode, GenericMessage> reportedPolicy,
            ShadowDesiredPolicy<JsonNode, GenericMessage> desiredPolicy, VersionSafeExecutor executor,
            EdgeDriver<JsonNode, GenericMessage> driver) {
        super(rootTopic, reportedPolicy, desiredPolicy, executor, driver);
    }

}
