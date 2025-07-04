package io.tinga.b3.agent;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.b3.agent.security.Operation;
import io.tinga.b3.agent.security.impl.OperationJsonSchemaChecker;
import io.tinga.b3.agent.shadowing.policy.EdgeFirstShadowDesiredPolicy;
import io.tinga.b3.agent.shadowing.policy.EdgeFirstShadowReportedPolicy;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.helpers.JsonSchemaProvider;
import io.tinga.b3.helpers.jsonschema.JsonSchemaResourcesProvider;

public class EntityAgentCommandExecutorMQTTModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Operation.GrantsChecker.class).to(OperationJsonSchemaChecker.class);
        bind(JsonSchemaProvider.class).to(JsonSchemaResourcesProvider.class);
        // bind(B3MessageProvider.class).to(DummyB3MessageProvider.class);
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
