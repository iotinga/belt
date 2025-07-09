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
import io.tinga.b3.agent.security.Operation;
import io.tinga.b3.agent.security.impl.DisabledGrantsChecker;
import io.tinga.b3.agent.security.impl.StandardOperationFactory;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor;
import io.tinga.b3.agent.shadowing.impl.RetainedReportedVersionSafeExecutor;
import io.tinga.b3.agent.shadowing.policy.EdgeFirstShadowDesiredPolicy;
import io.tinga.b3.agent.shadowing.policy.EdgeFirstShadowReportedPolicy;
import io.tinga.b3.helpers.AgentProxy;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.helpers.proxy.CachedAgentProxyFactory;
import io.tinga.b3.helpers.proxy.StaticTopicBasedAgentProxy;
import io.tinga.b3.protocol.B3ITopicFactoryProxy;
import io.tinga.b3.protocol.impl.PassthroughITopicFactoryProxy;
import io.tinga.belt.input.GadgetCommandExecutor;

public class GroupAgentCommandExecutorMQTTModule extends AbstractModule {

    private final GroupAgentCommand command;

    public GroupAgentCommandExecutorMQTTModule(GroupAgentCommand command) {
        this.command = command;
    }

    @Override
    protected void configure() {
        // BAUER (MQTT)
        bind(Key.get(new TypeLiteral<AgentProxy.Factory<GenericB3Message>>() {
        })).to(Key.get(new TypeLiteral<CachedAgentProxyFactory<GenericB3Message>>() {
        }));
        bind(Key.get(new TypeLiteral<AgentProxy<GenericB3Message>>() {
        })).to(new TypeLiteral<StaticTopicBasedAgentProxy<GenericB3Message>>() {
        });
        bind(B3ITopicFactoryProxy.class).to(PassthroughITopicFactoryProxy.class).in(Singleton.class);

        // COMMAND EXECUTOR
        bind(GroupAgentCommand.class).toInstance(this.command);
        bind(GadgetCommandExecutor.class).to(GroupAgentCommandExecutor.class);

        // DRIVER
        bind(Key.get(new TypeLiteral<ShadowDesiredPreProcessor<GenericB3Message>>() {
        })).to(EdgeGroupAgentDesiredPreProcessor.class);
        bind(Key.get(new TypeLiteral<ShadowReportedPostProcessor<GenericB3Message>>() {
        })).to(EdgeGroupAgentReportedPostProcessor.class);
        bind(Key.get(new TypeLiteral<Agent.EdgeDriver<GenericB3Message>>() {
        })).to(GroupAgentEdgeDriver.class).in(Singleton.class);

        // VERSION MANAGEMENT
        bind(VersionSafeExecutor.class).to(Key.get(new TypeLiteral<RetainedReportedVersionSafeExecutor<GenericB3Message>>() {
        })).in(Singleton.class);

        // SECURITY
        bind(Operation.Factory.class).to(StandardOperationFactory.class);
        bind(Key.get(new TypeLiteral<Operation.GrantsChecker<GenericB3Message>>() {
        })).to(Key.get(new TypeLiteral<DisabledGrantsChecker<GenericB3Message>>() {
        }));

        // SHADOWING POLICIES
        bind(Key.get(new TypeLiteral<Agent.ShadowDesiredPolicy<GenericB3Message>>() {
        })).to(Key.get(new TypeLiteral<EdgeFirstShadowDesiredPolicy<GenericB3Message>>() {
        }));
        bind(Key.get(new TypeLiteral<Agent.ShadowReportedPolicy<GenericB3Message>>() {
        })).to(Key.get(new TypeLiteral<EdgeFirstShadowReportedPolicy<GenericB3Message>>() {
        }));
    }

}
