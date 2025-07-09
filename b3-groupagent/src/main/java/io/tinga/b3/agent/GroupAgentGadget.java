package io.tinga.b3.agent;

import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import io.tinga.b3.protocol.impl.StandardB3TopicFactory;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.belt.AbstractGadget;
import io.tinga.belt.config.ConfigurationProvider;
import io.tinga.belt.input.GadgetCommandOption;
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
        bind(Key.get(new TypeLiteral<Class<GenericB3Message>>() {
        })).toInstance(GenericB3Message.class);

        bind(B3Topic.Factory.class).to(StandardB3TopicFactory.class);

    }

    @Provides
    public B3Topic.Base buildAgentTopic(B3Topic.Factory topicBaseFactory, GroupAgentConfig config) {
        return topicBaseFactory.agent(config.agentId());
    }

    @Provides
    @Singleton
    public GroupAgentConfig buildGadgetConfig(ConfigurationProvider provider) {
        if (config == null) {
            config = provider.config(GroupAgentGadget.NAME.toUpperCase(), GroupAgentConfigImpl.class);
        }
        return config;
    }

    @Provides
    @Singleton
    public Agent.Config buildAgentConfig(ConfigurationProvider provider) {
        if (config == null) {
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
        switch (command.action()) {
            default:
            case MQTT:
                Module[] mqttModules = { TopicFactory.getAsModule(properties),
                        new GroupAgentCommandExecutorMQTTModule(command) };
                return mqttModules;
        }
    }
}
