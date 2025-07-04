package io.tinga.b3.agent;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.b3.agent.shadowing.Operation;
import io.tinga.b3.agent.shadowing.impl.EdgeFirstShadowReportedPolicy;
import io.tinga.b3.agent.shadowing.impl.OperationJsonSchemaChecker;
import io.tinga.b3.agent.shadowing.impl.PassthroughShadowDesiredPolicy;
import io.tinga.b3.helpers.B3MessageProvider;
import io.tinga.b3.helpers.FromFileB3MessageProvider;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.helpers.StdinB3MessageProvider;
import io.tinga.b3.helpers.jsonschema.JsonSchemaFromFileProvider;
import io.tinga.b3.helpers.jsonschema.JsonSchemaProvider;

public class EntityAgentCommandExecutorFilesystemModule extends AbstractModule {

    private final EntityAgentCommand command;

    public EntityAgentCommandExecutorFilesystemModule(EntityAgentCommand command) {
        this.command = command;
    }

    @Override
    protected void configure() {
        bind(EntityAgentCommand.class).toInstance(command);

        bind(Operation.GrantsChecker.class).to(OperationJsonSchemaChecker.class);
        bind(Key.get(new TypeLiteral<GadgetCommandExecutor<EntityAgentCommand>>() {
        })).to(EntityAgentCommandExecutorOnce.class);

        bind(JsonSchemaProvider.class).to(JsonSchemaFromFileProvider.class);
        bind(Key.get(new TypeLiteral<Agent.ShadowDesiredPolicy<GenericB3Message>>() {
        })).to(Key.get(new TypeLiteral<PassthroughShadowDesiredPolicy<GenericB3Message>>() {
        }));

        bind(Key.get(new TypeLiteral<Agent.ShadowReportedPolicy<GenericB3Message>>() {
        })).to(Key.get(new TypeLiteral<EdgeFirstShadowReportedPolicy<GenericB3Message>>() {
        }));

        if (command.desiredRef() == null) {
            bind(B3MessageProvider.class).to(StdinB3MessageProvider.class);
        } else {
            bind(B3MessageProvider.class).to(FromFileB3MessageProvider.class);
        }
    }

}