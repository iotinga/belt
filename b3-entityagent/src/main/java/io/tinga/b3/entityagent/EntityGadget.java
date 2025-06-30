package io.tinga.b3.entityagent;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;

import io.tinga.belt.AbstractGadget;
import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.belt.input.GadgetCommandOption;
import io.tinga.belt.output.GadgetInMemoryPlainTextSink;
import io.tinga.belt.output.GadgetSink;
import io.tinga.b3.entityagent.cli.EntityCliCommandExecutorModule;
import io.tinga.b3.entityagent.operation.EntityOperationFactory;
import io.tinga.b3.entityagent.operation.EntityOperationTopicBasedFactory;
import io.tinga.b3.protocol.B3MessageValidator;
import io.tinga.b3.protocol.B3MessageVersionBasedValidator;
import it.netgrid.bauer.TopicFactory;

public class EntityGadget extends AbstractGadget<EntityCommand> {
    private static final Logger log = LoggerFactory.getLogger(EntityGadget.class);

    public static final String NAME = "ENTITY";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    protected void configure() {
        bind(GadgetSink.class).to(GadgetInMemoryPlainTextSink.class);
        bind(JsonSchemaFactory.class).toInstance(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7));
        bind(EntityOperationFactory.class).to(EntityOperationTopicBasedFactory.class);
        bind(B3MessageValidator.class).to(B3MessageVersionBasedValidator.class);
        bind(Key.get(new TypeLiteral<GadgetCommandExecutor<EntityCommand>>(){})).to(EntityCommandExecutor.class);
    }

    @Override
    public Class<EntityCommand> commandClass() {
        return EntityCommand.class;
    }

    @Override
    public List<GadgetCommandOption> commandOptions() {
        return Arrays.asList(EntityCommandOption.values());
    }

    @Override
    public Module[] buildExecutorModules(Properties properties, EntityCommand command) {
        log.debug("Building executor modules with properties {}", properties);
        if (command == null || command.mode() == EntityInputMode.MQTT) {
            Module[] retval = { TopicFactory.getAsModule(properties), new EntityCommandExecutorModule() };
            return retval;
        } else {
            Module[] retval = { new EntityCliCommandExecutorModule(command) };
            return retval;
        }
    }

}
