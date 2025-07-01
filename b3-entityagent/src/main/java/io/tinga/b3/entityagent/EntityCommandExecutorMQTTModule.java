package io.tinga.b3.entityagent;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.b3.core.Agent;
import io.tinga.b3.entityagent.desired.DesiredEntityMessageDummyProvider;
import io.tinga.b3.entityagent.desired.DesiredEntityMessageProvider;
import io.tinga.b3.entityagent.jsonschema.JsonSchemaProvider;
import io.tinga.b3.entityagent.jsonschema.JsonSchemaResourcesProvider;
import io.tinga.b3.entityagent.operation.EntityMessage;
import io.tinga.b3.entityagent.operation.EntityOperationGrantsChecker;
import io.tinga.b3.entityagent.operation.EntityOperationJsonSchemaChecker;
import io.tinga.b3.entityagent.reported.ReportedRetainedMessagesStore;
import io.tinga.b3.entityagent.reported.ReportedStore;
import io.tinga.b3.entityagent.shadowing.RoleBasedEdgeFirstDesiredPolicy;

public class EntityCommandExecutorMQTTModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EntityOperationGrantsChecker.class).to(EntityOperationJsonSchemaChecker.class);
        bind(ReportedStore.class).to(ReportedRetainedMessagesStore.class);
        bind(JsonSchemaProvider.class).to(JsonSchemaResourcesProvider.class);
        bind(DesiredEntityMessageProvider.class).to(DesiredEntityMessageDummyProvider.class);
        bind(Key.get(new TypeLiteral<Agent.ShadowDesiredPolicy<EntityMessage>>(){})).to(RoleBasedEdgeFirstDesiredPolicy.class);

        bind(Key.get(new TypeLiteral<GadgetCommandExecutor<EntityCommand>>(){})).to(EntityCommandExecutorDaemon.class);
    }

}
