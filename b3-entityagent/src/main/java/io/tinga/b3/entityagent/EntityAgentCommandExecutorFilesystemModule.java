package io.tinga.b3.entityagent;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.b3.core.Agent;
import io.tinga.b3.core.helpers.B3MessageFromFileProvider;
import io.tinga.b3.core.helpers.B3MessageProvider;
import io.tinga.b3.core.helpers.B3MessageStdinProvider;
import io.tinga.b3.core.shadowing.SinkShadowReportedPolicy;
import io.tinga.b3.entityagent.jsonschema.JsonSchemaFromFileProvider;
import io.tinga.b3.entityagent.jsonschema.JsonSchemaProvider;
import io.tinga.b3.entityagent.operation.OperationGrantsChecker;
import io.tinga.b3.entityagent.operation.OperationJsonSchemaChecker;
import io.tinga.b3.protocol.GenericB3Message;

public class EntityAgentCommandExecutorFilesystemModule extends AbstractModule {

    private final EntityAgentCommand command;

    public EntityAgentCommandExecutorFilesystemModule(EntityAgentCommand command) {
        this.command = command;
    }

    @Override
    protected void configure() {
        bind(EntityAgentCommand.class).toInstance(command);
        bind(OperationGrantsChecker.class).to(OperationJsonSchemaChecker.class);
        bind(Key.get(new TypeLiteral<GadgetCommandExecutor<EntityAgentCommand>>() {
        })).to(EntityAgentCommandExecutorOnce.class);

        bind(JsonSchemaProvider.class).to(JsonSchemaFromFileProvider.class);
        bind(Key.get(new TypeLiteral<Agent.ShadowReportedPolicy<GenericB3Message>>() {
        })).to(Key.get(new TypeLiteral<SinkShadowReportedPolicy<GenericB3Message>>() {
        }));

        // bind(ReportedStore.class).to(ReportedFromFileReadOnlyStore.class);

        if (command.desiredRef() == null) {
            bind(B3MessageProvider.class).to(B3MessageStdinProvider.class);
        } else {
            bind(B3MessageProvider.class).to(B3MessageFromFileProvider.class);
        }
    }

}