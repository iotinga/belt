package io.tinga.b3.groupagent.cli;

import java.util.ArrayList;
import java.util.List;

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
import io.tinga.b3.groupagent.GroupAgentCommand;
import io.tinga.b3.groupagent.GroupAgentConfig;
import io.tinga.b3.groupagent.GroupAgentEdgeDriver;
import io.tinga.b3.groupagent.processors.EdgeGroupDesiredPreProcessor;
import io.tinga.b3.groupagent.processors.EdgeGroupReportedPostProcessor;
import io.tinga.b3.protocol.GenericMessage;
import io.tinga.b3.protocol.topic.BasicTopicNameFactory;
import io.tinga.b3.protocol.topic.RootTopic;
import io.tinga.b3.protocol.topic.TopicNameFactory;

public class GroupAgentCliCommandExecutorModule extends AbstractModule {

    private final GroupAgentCommand command;

    public GroupAgentCliCommandExecutorModule(GroupAgentCommand command) {
        this.command = command;
    }

    protected void configure() {

        bind(VersionSafeExecutor.class).to(InitFromReportedTopicVersionSafeExecutor.class).in(Singleton.class);

        bind(GroupAgentCommand.class).toInstance(this.command);

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
    public GroupAgentConfig buildGadgetConfiguration() {

        return new GroupAgentConfig() {

            @Override
            public String roleInMembers() {
                return command.roleInMembers();
            }

            @Override
            public String membersIds() {
                return command.membersIds();
            }

            @Override
            public List<String> members() {
                List<String> retval;
                if (command.membersIds() != null && command.membersIds().trim().length() > 0) {
                    retval = List.of(command.membersIds().trim().split(GROUPAGENT_MEMBERS_IDS_GLUE));
                } else {
                    retval = new ArrayList<>();
                }
                return retval;
            }

        };
    }

}