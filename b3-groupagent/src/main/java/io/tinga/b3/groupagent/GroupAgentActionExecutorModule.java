package io.tinga.b3.groupagent;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.shadowing.ShadowDesiredPreProcessor;
import io.tinga.b3.core.shadowing.ShadowReportedPostProcessor;
import io.tinga.b3.groupagent.driver.GroupAgentEdgeDriver;
import io.tinga.b3.groupagent.driver.processors.EdgeGroupDesiredPreProcessor;
import io.tinga.b3.groupagent.driver.processors.EdgeGroupReportedPostProcessor;
import io.tinga.b3.protocol.GenericMessage;

public class GroupAgentActionExecutorModule extends AbstractModule {

    private final GroupAgentCommand command;

    public GroupAgentActionExecutorModule(GroupAgentCommand command) {
        this.command = command;
    }

    @Override
    protected void configure() {
        bind(GroupAgentCommand.class).toInstance(this.command);

        bind(Key.get(new TypeLiteral<ShadowDesiredPreProcessor<GenericMessage>>() {
        })).to(EdgeGroupDesiredPreProcessor.class);
        bind(Key.get(new TypeLiteral<ShadowReportedPostProcessor<GenericMessage>>() {
        })).to(EdgeGroupReportedPostProcessor.class);
        bind(Key.get(new TypeLiteral<EdgeDriver<ObjectNode, GenericMessage>>() {
        })).to(GroupAgentEdgeDriver.class).in(Singleton.class);
    }

}
