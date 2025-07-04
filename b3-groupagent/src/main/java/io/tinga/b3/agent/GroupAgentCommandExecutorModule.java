package io.tinga.b3.agent;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import io.tinga.b3.agent.driver.GroupAgentEdgeDriver;
import io.tinga.b3.agent.driver.ShadowDesiredPreProcessor;
import io.tinga.b3.agent.driver.ShadowReportedPostProcessor;
import io.tinga.b3.agent.driver.processors.EdgeGroupAgentDesiredPreProcessor;
import io.tinga.b3.agent.driver.processors.EdgeGroupAgentReportedPostProcessor;
import io.tinga.b3.helpers.GenericB3Message;
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
