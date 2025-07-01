package io.tinga.b3.groupagent;

import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import io.tinga.b3.core.Agent;
import io.tinga.b3.core.AgentProxy;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.core.impl.AgentProxyFactoryImpl;
import io.tinga.b3.core.impl.GenericAgentProxy;
import io.tinga.b3.core.impl.InitFromReportedTopicVersionSafeExecutor;
import io.tinga.b3.core.impl.SingletonsITopicFactoryProxy;
import io.tinga.b3.core.shadowing.GenericEdgeFirstShadowDesiredPolicy;
import io.tinga.b3.core.shadowing.GenericEdgeFirstShadowReportedPolicy;
import io.tinga.b3.protocol.GenericB3Message;
import io.tinga.b3.protocol.topic.B3Topic;
import io.tinga.b3.protocol.topic.BasicTopicNameFactory;
import io.tinga.b3.protocol.topic.B3TopicRoot;
import io.tinga.b3.protocol.topic.TopicNameFactory;
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
        bind(ITopicFactoryProxy.class).to(SingletonsITopicFactoryProxy.class).in(Singleton.class);
        bind(Key.get(new TypeLiteral<AgentProxy<GenericB3Message>>() {
        })).to(GenericAgentProxy.class);
        bind(Key.get(new TypeLiteral<AgentProxy.Factory>() {
        })).to(AgentProxyFactoryImpl.class);
        bind(TopicNameFactory.class).to(BasicTopicNameFactory.class);

        bind(Key.get(new TypeLiteral<Agent.ShadowDesiredPolicy<GenericB3Message>>() {
        })).to(GenericEdgeFirstShadowDesiredPolicy.class);
        bind(Key.get(new TypeLiteral<Agent.ShadowReportedPolicy<GenericB3Message>>() {
        })).to(GenericEdgeFirstShadowReportedPolicy.class);
    }

    @Provides
    public B3TopicRoot buildRootTopic(TopicNameFactory topicNameFactory) {
        return topicNameFactory.root();
    }

    @Provides
    public B3Topic buildAgentTopic(TopicNameFactory topicNameFactory, GroupAgentConfig config) {
        return topicNameFactory.root().agent(config.agentId());
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
