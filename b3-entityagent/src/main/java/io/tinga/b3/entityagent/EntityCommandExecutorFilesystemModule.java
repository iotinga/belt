package io.tinga.b3.entityagent;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.belt.output.GadgetSink;
import io.tinga.b3.core.Agent;
import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.shadowing.SinkShadowReportedPolicy;
import io.tinga.b3.entityagent.desired.DesiredGenericB3MessageFromFileProvider;
import io.tinga.b3.entityagent.desired.DesiredGenericB3MessageProvider;
import io.tinga.b3.entityagent.desired.DesiredGenericB3MessageStdinProvider;
import io.tinga.b3.entityagent.jsonschema.JsonSchemaFromFileProvider;
import io.tinga.b3.entityagent.jsonschema.JsonSchemaProvider;
import io.tinga.b3.protocol.GenericB3Message;
import io.tinga.b3.entityagent.operation.EntityOperationGrantsChecker;
import io.tinga.b3.entityagent.operation.EntityOperationJsonSchemaChecker;
import io.tinga.b3.entityagent.reported.ReportedFromFileReadOnlyStore;
import io.tinga.b3.entityagent.reported.ReportedStore;

public class EntityCommandExecutorFilesystemModule extends AbstractModule {

    private final EntityCommand command;

    public EntityCommandExecutorFilesystemModule(EntityCommand command) {
        this.command = command;
    }

    @Override
    protected void configure() {
        bind(EntityCommand.class).toInstance(command);
        bind(EntityOperationGrantsChecker.class).to(EntityOperationJsonSchemaChecker.class);
        bind(Key.get(new TypeLiteral<GadgetCommandExecutor<EntityCommand>>() {
        })).to(EntityCommandExecutorOnce.class);

        bind(JsonSchemaProvider.class).to(JsonSchemaFromFileProvider.class);
        bind(ReportedStore.class).to(ReportedFromFileReadOnlyStore.class);

        if (command.desiredRef() == null) {
            bind(DesiredGenericB3MessageProvider.class).to(DesiredGenericB3MessageStdinProvider.class);
        } else {
            bind(DesiredGenericB3MessageProvider.class).to(DesiredGenericB3MessageFromFileProvider.class);
        }
    }

    @Provides
    @Singleton
    public Agent.ShadowReportedPolicy<GenericB3Message> buildShadowReportedPolicy(final GadgetSink sink,
            final EdgeDriver<GenericB3Message> driver) {
        return new SinkShadowReportedPolicy<GenericB3Message>(sink, driver) {
            @Override
            public Class<GenericB3Message> getEventClass() {
                return GenericB3Message.class;
            }

        };
    }

}