package io.tinga.b3.entityagent;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.b3.core.Agent;
import io.tinga.b3.core.helpers.B3MessageDummyProvider;
import io.tinga.b3.core.helpers.B3MessageProvider;
import io.tinga.b3.entityagent.jsonschema.JsonSchemaProvider;
import io.tinga.b3.entityagent.jsonschema.JsonSchemaResourcesProvider;
import io.tinga.b3.protocol.GenericB3Message;
import io.tinga.b3.entityagent.operation.OperationGrantsChecker;
import io.tinga.b3.entityagent.operation.OperationJsonSchemaChecker;
import io.tinga.b3.entityagent.shadowing.RoleBasedEdgeFirstDesiredPolicy;

public class EntityAgentCommandExecutorMQTTModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(OperationGrantsChecker.class).to(OperationJsonSchemaChecker.class);
        bind(JsonSchemaProvider.class).to(JsonSchemaResourcesProvider.class);
        bind(B3MessageProvider.class).to(B3MessageDummyProvider.class);
        bind(Key.get(new TypeLiteral<Agent.ShadowDesiredPolicy<GenericB3Message>>(){})).to(RoleBasedEdgeFirstDesiredPolicy.class);
        bind(Key.get(new TypeLiteral<GadgetCommandExecutor<EntityAgentCommand>>(){})).to(EntityAgentCommandExecutorDaemon.class);
    }

}
