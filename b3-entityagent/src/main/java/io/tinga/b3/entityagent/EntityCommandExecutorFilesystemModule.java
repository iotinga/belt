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
import io.tinga.b3.entityagent.desired.DesiredEntityMessageBasicHandler;
import io.tinga.b3.entityagent.desired.DesiredEntityMessageFromFileProvider;
import io.tinga.b3.entityagent.desired.DesiredEntityMessageHandler;
import io.tinga.b3.entityagent.desired.DesiredEntityMessageProvider;
import io.tinga.b3.entityagent.desired.DesiredEntityMessageStdinProvider;
import io.tinga.b3.entityagent.jsonschema.JsonSchemaFromFileProvider;
import io.tinga.b3.entityagent.jsonschema.JsonSchemaProvider;
import io.tinga.b3.entityagent.operation.EntityMessage;
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
        bind(DesiredEntityMessageHandler.class).to(DesiredEntityMessageBasicHandler.class);
        bind(Key.get(new TypeLiteral<GadgetCommandExecutor<EntityCommand>>() {
        })).to(EntityCommandExecutorOnce.class);

        bind(JsonSchemaProvider.class).to(JsonSchemaFromFileProvider.class);
        bind(ReportedStore.class).to(ReportedFromFileReadOnlyStore.class);

        if (command.desiredRef() == null) {
            bind(DesiredEntityMessageProvider.class).to(DesiredEntityMessageStdinProvider.class);
        } else {
            bind(DesiredEntityMessageProvider.class).to(DesiredEntityMessageFromFileProvider.class);
        }
    }

    @Provides
    @Singleton
    public Agent.ShadowReportedPolicy<EntityMessage> buildShadowReportedPolicy(final GadgetSink sink,
            final EdgeDriver<EntityMessage> driver) {
        return new SinkShadowReportedPolicy<EntityMessage>(sink, driver) {
            @Override
            public Class<EntityMessage> getEventClass() {
                return EntityMessage.class;
            }

        };
    }

}