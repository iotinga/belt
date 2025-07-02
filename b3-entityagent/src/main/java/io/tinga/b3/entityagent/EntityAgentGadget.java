package io.tinga.b3.entityagent;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;

import io.tinga.belt.AbstractGadget;
import io.tinga.belt.config.ConfigurationProvider;
import io.tinga.belt.input.GadgetCommandOption;
import io.tinga.belt.output.GadgetInMemoryPlainTextSink;
import io.tinga.belt.output.GadgetSink;
import io.tinga.b3.core.shadowing.operation.OperationFactory;
import io.tinga.b3.core.shadowing.operation.OperationTopicBasedFactory;
import io.tinga.b3.protocol.B3MessageValidator;
import io.tinga.b3.protocol.B3MessageVersionBasedValidator;
import io.tinga.b3.protocol.GenericB3Message;
import io.tinga.b3.protocol.topic.B3TopicFactory;
import io.tinga.b3.protocol.topic.B3TopicFactoryImpl;
import it.netgrid.bauer.TopicFactory;

public class EntityAgentGadget extends AbstractGadget<EntityAgentCommand> {
    private static final Logger log = LoggerFactory.getLogger(EntityAgentGadget.class);

    public static final String NAME = "ENTITY";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    protected void configure() {
        bind(Key.get(new TypeLiteral<Class<GenericB3Message>>(){})).toInstance(GenericB3Message.class);
       
        bind(GadgetSink.class).to(GadgetInMemoryPlainTextSink.class);
        bind(JsonSchemaFactory.class).toInstance(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7));
        bind(OperationFactory.class).to(OperationTopicBasedFactory.class);
        bind(B3MessageValidator.class).to(B3MessageVersionBasedValidator.class);
        bind(B3TopicFactory.class).to(B3TopicFactoryImpl.class);

    }

    @Override
    public Class<EntityAgentCommand> commandClass() {
        return EntityAgentCommand.class;
    }

    @Override
    public List<GadgetCommandOption> commandOptions() {
        return Arrays.asList(EntityAgentCommandOption.values());
    }

    @Provides
    @Singleton
    public EntityAgentConfig buildGadgetConfig(ConfigurationProvider provider) {
        return provider.config(EntityAgentGadget.NAME.toUpperCase(), EntityAgentConfigImpl.class);
    }

    @Override
    public Module[] buildExecutorModules(Properties properties, EntityAgentCommand command) {
        log.debug("Building executor modules with properties {}", properties);
        switch (command.action()) {
            case MQTT:
                Module[] mqttModules = { TopicFactory.getAsModule(properties), new EntityAgentCommandExecutorMQTTModule() };
                return mqttModules;
            case FILESYSTEM:
                Module[] filesystemModules = { new EntityAgentCommandExecutorFilesystemModule(command) };
                return filesystemModules;
            case RESOURCES:
            default:
                Module[] resourcesModules = { new EntityAgentCommandExecutorResourcesModule(command) };
                return resourcesModules;
        }
    }

}
