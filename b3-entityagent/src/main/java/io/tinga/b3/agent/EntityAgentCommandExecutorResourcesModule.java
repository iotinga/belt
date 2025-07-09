package io.tinga.b3.agent;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.b3.agent.security.Operation;
import io.tinga.b3.agent.security.impl.JsonSchemaOperationGrantsChecker;
import io.tinga.b3.agent.shadowing.policy.SinkShadowReportedPolicy;
import io.tinga.b3.helpers.B3MessageProvider;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.helpers.JsonSchemaProvider;
import io.tinga.b3.helpers.jsonschema.JsonSchemaResourcesProvider;
import io.tinga.b3.helpers.messageprovider.ResourcesB3MessageProvider;
import io.tinga.b3.helpers.messageprovider.StdinB3MessageProvider;

public class EntityAgentCommandExecutorResourcesModule extends AbstractModule {

    private final EntityAgentCommand command;

    public EntityAgentCommandExecutorResourcesModule(EntityAgentCommand command) {
        this.command = command;
    }

    @Override
    protected void configure() {
        bind(EntityAgentCommand.class).toInstance(command);
        bind(Operation.GrantsChecker.class).to(JsonSchemaOperationGrantsChecker.class);
        bind(GadgetCommandExecutor.class).to(EntityAgentCommandExecutorOnce.class);
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