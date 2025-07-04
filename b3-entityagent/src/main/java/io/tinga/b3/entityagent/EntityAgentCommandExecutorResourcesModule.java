package io.tinga.b3.entityagent;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.b3.core.Agent;
import io.tinga.b3.core.helpers.B3MessageProvider;
import io.tinga.b3.core.helpers.ResourcesB3MessageProvider;
import io.tinga.b3.core.helpers.StdinB3MessageProvider;
import io.tinga.b3.core.helpers.jsonschema.JsonSchemaProvider;
import io.tinga.b3.core.helpers.jsonschema.JsonSchemaResourcesProvider;
import io.tinga.b3.core.shadowing.Operation;
import io.tinga.b3.core.shadowing.impl.OperationJsonSchemaChecker;
import io.tinga.b3.core.shadowing.impl.SinkShadowReportedPolicy;
import io.tinga.b3.protocol.GenericB3Message;

public class EntityAgentCommandExecutorResourcesModule extends AbstractModule {

    private final EntityAgentCommand command;

    public EntityAgentCommandExecutorResourcesModule(EntityAgentCommand command) {
        this.command = command;
    }

    @Override
    protected void configure() {
        bind(EntityAgentCommand.class).toInstance(command);
        bind(Operation.GrantsChecker.class).to(OperationJsonSchemaChecker.class);
        bind(Key.get(new TypeLiteral<GadgetCommandExecutor<EntityAgentCommand>>() {
        })).to(EntityAgentCommandExecutorOnce.class);
        bind(Key.get(new TypeLiteral<Agent.ShadowReportedPolicy<GenericB3Message>>() {
        })).to(Key.get(new TypeLiteral<SinkShadowReportedPolicy<GenericB3Message>>() {
        }));

        bind(JsonSchemaProvider.class).to(JsonSchemaResourcesProvider.class);
        // bind(ReportedStore.class).to(ReportedResourcesReadOnlyStore.class);
        if (command.desiredRef() == null) {
            bind(B3MessageProvider.class).to(StdinB3MessageProvider.class);
        } else {
            bind(B3MessageProvider.class).to(ResourcesB3MessageProvider.class);
        }
    }

}