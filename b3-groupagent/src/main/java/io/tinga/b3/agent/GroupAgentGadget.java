package io.tinga.b3.agent;

import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import io.tinga.b3.protocol.impl.B3TopicFactoryImpl;
import io.tinga.b3.protocol.impl.PassthroughITopicFactoryProxy;
import io.tinga.b3.agent.driver.AgentProxy;
import io.tinga.b3.agent.driver.impl.AgentProxyFactoryImpl;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor;
import io.tinga.b3.agent.shadowing.impl.InitFromReportedTopicVersionSafeExecutor;
import io.tinga.b3.protocol.B3ITopicFactoryProxy;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.belt.AbstractGadget;
import io.tinga.belt.config.ConfigurationProvider;
import io.tinga.belt.input.GadgetCommandOption;
import io.tinga.belt.output.GadgetInMemoryPlainTextSink;
import io.tinga.belt.output.GadgetSink;
import it.netgrid.bauer.TopicFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class GroupAgentGadget extends AbstractGadget<GroupAgentCommand> {
    Logger log = LoggerFactory.getLogger(GroupAgentGadget.class);

    public static final String NAME = "GROUPAGENT";

    public static GroupAgentConfig config;
    
    protected void configure() {
        bind(GadgetSink.class).to(GadgetInMemoryPlainTextSink.class);

        bind(VersionSafeExecutor.class).to(InitFromReportedTopicVersionSafeExecutor.class).in(Singleton.class);

        // FIELD PROXIES
        bind(B3ITopicFactoryProxy.class).to(PassthroughITopicFactoryProxy.class).in(Singleton.class);
        // bind(Key.get(new TypeLiteral<AgentProxy<GenericB3Message>>() {
        // })).to(GenericAgentProxy.class);
        bind(Key.get(new TypeLiteral<AgentProxy.Factory>() {
        })).to(AgentProxyFactoryImpl.class);
        bind(B3Topic.Factory.class).to(B3TopicFactoryImpl.class);

        // bind(Key.get(new TypeLiteral<Agent.ShadowDesiredPolicy<GenericB3Message>>() {
        // })).to(GenericEdgeFirstShadowDesiredPolicy.class);
        // bind(Key.get(new TypeLiteral<Agent.ShadowReportedPolicy<GenericB3Message>>() {
        // })).to(GenericEdgeFirstShadowReportedPolicy.class);
    }


    @Provides
    public B3Topic.Root buildAgentTopic(B3Topic.Factory topicRootFactory, GroupAgentConfig config) {
        return topicRootFactory.agent(config.agentId());
    }

    @Provides
    @Singleton
    public GroupAgentConfig buildGadgetConfig(ConfigurationProvider provider) {
        if(config == null) {
            config = provider.config(GroupAgentGadget.NAME.toUpperCase(), GroupAgentConfigImpl.class);
        }
        return config;
    }

    @Provides
    @Singleton
    public Agent.Config buildAgentConfig(ConfigurationProvider provider) {
        if(config == null) {
            config = provider.config(GroupAgentGadget.NAME.toUpperCase(), GroupAgentConfigImpl.class);
        }
        return config;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Class<GroupAgentCommand> commandClass() {
        return GroupAgentCommand.class;
    }

    @Override
    public List<GadgetCommandOption> commandOptions() {
        return Arrays.asList(GroupAgentCommandOption.values());
    }

    @Override
    public com.google.inject.Module[] buildExecutorModules(Properties properties, GroupAgentCommand command) {
        log.debug("Building executor modules with properties {}", properties);
        Module[] retval = {TopicFactory.getAsModule(properties), new GroupAgentCommandExecutorModule(command)};
        return retval;
    }
}
