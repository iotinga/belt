package io.tinga.b3.agent;

import com.google.inject.Inject;

import io.tinga.b3.agent.driver.AgentProxy.Factory;
import io.tinga.b3.agent.impl.AbstractAgentCommandExecutor;
import io.tinga.b3.agent.security.Operation.GrantsChecker;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.protocol.B3Topic.Base;
import io.tinga.belt.output.Status;

public class GroupAgentCommandExecutor extends AbstractAgentCommandExecutor<GenericB3Message, GroupAgentCommand> {

    @Inject
    public GroupAgentCommandExecutor(Factory agentProxyFactory, Base topicBase,
            ShadowReportedPolicy<GenericB3Message> reportedPolicy, ShadowDesiredPolicy<GenericB3Message> desiredPolicy,
            VersionSafeExecutor executor, GrantsChecker<GenericB3Message> grantsChecker,
            EdgeDriver<GenericB3Message> driver) {
        super(agentProxyFactory, topicBase, reportedPolicy, desiredPolicy, executor, grantsChecker, driver);
    }

    @Override
    public Status execute(GroupAgentCommand command) {
        // NOTHING TO DO
        return Status.OK;
    }

}
