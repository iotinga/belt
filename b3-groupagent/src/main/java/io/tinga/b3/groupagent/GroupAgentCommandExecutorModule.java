package io.tinga.b3.groupagent;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import io.tinga.b3.core.Agent;
import io.tinga.b3.core.driver.ShadowDesiredPreProcessor;
import io.tinga.b3.core.driver.ShadowReportedPostProcessor;
import io.tinga.b3.groupagent.driver.GroupAgentEdgeDriver;
import io.tinga.b3.groupagent.driver.processors.EdgeGroupAgentDesiredPreProcessor;
import io.tinga.b3.groupagent.driver.processors.EdgeGroupAgentReportedPostProcessor;
import io.tinga.b3.protocol.impl.GenericB3Message;
import io.tinga.belt.input.GadgetCommandExecutor;

public class GroupAgentCommandExecutorModule extends AbstractModule {

    private final GroupAgentCommand command;

    public GroupAgentCommandExecutorModule(GroupAgentCommand command) {
        this.command = command;
    }

    @Override
    protected void configure() {
        bind(GroupAgentCommand.class).toInstance(this.command);

        bind(Key.get(new TypeLiteral<ShadowDesiredPreProcessor<GenericB3Message>>() {
        })).to(EdgeGroupAgentDesiredPreProcessor.class);
        bind(Key.get(new TypeLiteral<ShadowReportedPostProcessor<GenericB3Message>>() {
        })).to(EdgeGroupAgentReportedPostProcessor.class);
        bind(Key.get(new TypeLiteral<Agent.EdgeDriver<GenericB3Message>>() {
        })).to(GroupAgentEdgeDriver.class).in(Singleton.class);

        bind(Key.get(new TypeLiteral<GadgetCommandExecutor<GroupAgentCommand>>() {
        })).to(GroupAgentCommandExecutor.class);
    }

}
