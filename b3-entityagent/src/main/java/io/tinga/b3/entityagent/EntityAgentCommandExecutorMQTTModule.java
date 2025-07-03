package io.tinga.b3.entityagent;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.b3.core.Agent;
import io.tinga.b3.core.OperationGrantsChecker;
import io.tinga.b3.core.helpers.DummyB3MessageProvider;
import io.tinga.b3.core.helpers.jsonschema.JsonSchemaProvider;
import io.tinga.b3.core.helpers.jsonschema.JsonSchemaResourcesProvider;
import io.tinga.b3.core.impl.OperationJsonSchemaChecker;
import io.tinga.b3.core.shadowing.impl.EdgeFirstShadowDesiredPolicy;
import io.tinga.b3.core.shadowing.impl.EdgeFirstShadowReportedPolicy;
import io.tinga.b3.core.helpers.B3MessageProvider;
import io.tinga.b3.protocol.GenericB3Message;

public class EntityAgentCommandExecutorMQTTModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(OperationGrantsChecker.class).to(OperationJsonSchemaChecker.class);
        bind(JsonSchemaProvider.class).to(JsonSchemaResourcesProvider.class);
        bind(B3MessageProvider.class).to(DummyB3MessageProvider.class);
        bind(Key.get(new TypeLiteral<Agent.ShadowDesiredPolicy<GenericB3Message>>() {
        })).to(Key.get(new TypeLiteral<EdgeFirstShadowDesiredPolicy<GenericB3Message>>() {
        }));

        bind(Key.get(new TypeLiteral<Agent.ShadowReportedPolicy<GenericB3Message>>() {
        })).to(Key.get(new TypeLiteral<EdgeFirstShadowReportedPolicy<GenericB3Message>>() {
        }));
        bind(Key.get(new TypeLiteral<GadgetCommandExecutor<EntityAgentCommand>>() {
        })).to(EntityAgentCommandExecutorDaemon.class);
    }

}
