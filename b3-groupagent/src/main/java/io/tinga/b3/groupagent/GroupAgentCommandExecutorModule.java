package io.tinga.b3.groupagent;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import io.tinga.b3.core.Agent;
import io.tinga.b3.core.AgentProxy;
import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.core.impl.AgentProxyFactoryImpl;
import io.tinga.b3.core.impl.GenericAgentProxy;
import io.tinga.b3.core.impl.InitFromReportedTopicVersionSafeExecutor;
import io.tinga.b3.core.impl.SingletonsITopicFactoryProxy;
import io.tinga.b3.core.shadowing.EdgeFirstShadowDesiredPolicy;
import io.tinga.b3.core.shadowing.EdgeFirstShadowReportedPolicy;
import io.tinga.b3.core.shadowing.ShadowDesiredPreProcessor;
import io.tinga.b3.core.shadowing.ShadowReportedPostProcessor;
import io.tinga.b3.groupagent.processors.EdgeGroupDesiredPreProcessor;
import io.tinga.b3.groupagent.processors.EdgeGroupReportedPostProcessor;
import io.tinga.b3.protocol.GenericMessage;
import io.tinga.b3.protocol.topic.BasicTopicNameFactory;
import io.tinga.b3.protocol.topic.RootTopic;
import io.tinga.b3.protocol.topic.TopicNameFactory;
import io.tinga.belt.config.ConfigurationProvider;

public class GroupAgentCommandExecutorModule extends AbstractModule {
    @Override
    protected void configure() {

        bind(VersionSafeExecutor.class).to(InitFromReportedTopicVersionSafeExecutor.class).in(Singleton.class);

        // FIELD PROXIES
        bind(ITopicFactoryProxy.class).to(SingletonsITopicFactoryProxy.class).in(Singleton.class);
        bind(Key.get(new TypeLiteral<AgentProxy<JsonNode, GenericMessage>>() {
        })).to(GenericAgentProxy.class);
        bind(Key.get(new TypeLiteral<AgentProxy.Factory>() {
        })).to(AgentProxyFactoryImpl.class);
        bind(TopicNameFactory.class).to(BasicTopicNameFactory.class);

        bind(Key.get(new TypeLiteral<Agent.ShadowDesiredPolicy<JsonNode, GenericMessage>>() {
        })).to(EdgeFirstShadowDesiredPolicy.class);
        bind(Key.get(new TypeLiteral<Agent.ShadowReportedPolicy<JsonNode, GenericMessage>>() {
        })).to(EdgeFirstShadowReportedPolicy.class);

        bind(Key.get(new TypeLiteral<ShadowDesiredPreProcessor<GenericMessage>>() {
        })).to(EdgeGroupDesiredPreProcessor.class);
        bind(Key.get(new TypeLiteral<ShadowReportedPostProcessor<GenericMessage>>() {
        })).to(EdgeGroupReportedPostProcessor.class);

        bind(Key.get(new TypeLiteral<EdgeDriver<JsonNode, GenericMessage>>() {
        })).to(GroupAgentEdgeDriver.class).in(Singleton.class);
    }

    @Provides
    public RootTopic buildRootTopic(TopicNameFactory topicNameFactory) {
        return topicNameFactory.root();
    }

    @Provides
    @Singleton
    public GroupAgentConfig buildGadgetConfig(ConfigurationProvider provider) {
        return provider.config(GroupAgentGadget.NAME.toUpperCase(), GroupAgentConfigImpl.class);
    }
}
