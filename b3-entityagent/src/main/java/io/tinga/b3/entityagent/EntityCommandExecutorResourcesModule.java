package io.tinga.b3.entityagent;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.b3.entityagent.desired.DesiredEntityMessageBasicHandler;
import io.tinga.b3.entityagent.desired.DesiredEntityMessageHandler;
import io.tinga.b3.entityagent.desired.DesiredEntityMessageProvider;
import io.tinga.b3.entityagent.desired.DesiredEntityMessageResourcesProvider;
import io.tinga.b3.entityagent.desired.DesiredEntityMessageStdinProvider;
import io.tinga.b3.entityagent.jsonschema.JsonSchemaProvider;
import io.tinga.b3.entityagent.jsonschema.JsonSchemaResourcesProvider;
import io.tinga.b3.entityagent.operation.EntityDummyOperationDaemon;
import io.tinga.b3.entityagent.operation.EntityOperationDaemon;
import io.tinga.b3.entityagent.operation.EntityOperationGrantsChecker;
import io.tinga.b3.entityagent.operation.EntityOperationJsonSchemaChecker;
import io.tinga.b3.entityagent.reported.ReportedResourcesReadOnlyStore;
import io.tinga.b3.entityagent.reported.ReportedStore;

public class EntityCommandExecutorResourcesModule extends AbstractModule {

    private final EntityCommand command;

    public EntityCommandExecutorResourcesModule(EntityCommand command) {
        this.command = command;
    }

    @Override
    protected void configure() {
        bind(GadgetCommandExecutor.class).to(EntityCommandExecutorMQTT.class);
        bind(EntityCommand.class).toInstance(command);
        bind(EntityOperationGrantsChecker.class).to(EntityOperationJsonSchemaChecker.class);
        bind(DesiredEntityMessageHandler.class).to(DesiredEntityMessageBasicHandler.class);
        bind(EntityOperationDaemon.class).to(EntityDummyOperationDaemon.class);
        bind(Key.get(new TypeLiteral<GadgetCommandExecutor<EntityCommand>>(){})).to(EntityCommandExecutorDefault.class);
        
        bind(JsonSchemaProvider.class).to(JsonSchemaResourcesProvider.class);
        bind(ReportedStore.class).to(ReportedResourcesReadOnlyStore.class);
        if (command.desiredRef() == null) {
            bind(DesiredEntityMessageProvider.class).to(DesiredEntityMessageStdinProvider.class);
        } else {
            bind(DesiredEntityMessageProvider.class).to(DesiredEntityMessageResourcesProvider.class);
        }
    }

    @Provides
    @Singleton
    public EntityConfig buildGadgetConfiguration() {
        return new EntityConfig() {

            @Override
            public String getJsonSchemaBasePath() {
                return command.schemaBaseDir();
            }

            @Override
            public String getReportedTopicFilter() {
                return command.topic();
            }

            @Override
            public String getDesiredTopicFilter() {
                return command.topic();
            }

            @Override
            public int getRetainedStoreWaitOnInitMillis() {
                return 0;
            }

            @Override
            public boolean isJsonSchemaCacheEnabled() {
                return false;
            }

            @Override
            public String getReportedStoreRef() {
                return command.reportedRef();
            }

        };
    }

}