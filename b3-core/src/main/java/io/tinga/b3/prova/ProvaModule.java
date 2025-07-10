package io.tinga.b3.prova;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import io.tinga.b3.agent.Agent;
import io.tinga.b3.agent.Agent.Config;
import io.tinga.b3.helpers.AgentProxy;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.helpers.proxy.ReadOnlyMessageProviderAgentProxy;
import io.tinga.b3.protocol.B3ITopicFactoryProxy;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.impl.PassthroughITopicFactoryProxy;
import io.tinga.b3.protocol.impl.StandardB3TopicFactory;

public class ProvaModule extends AbstractModule {

    protected void configure() {
        bind(Key.get(new TypeLiteral<Class<?>>() {
        })).toInstance(GenericB3Message.class);
        bind(Key.get(new TypeLiteral<Class<GenericB3Message>>() {
        })).toInstance(GenericB3Message.class);
        bind(Key.get(new TypeLiteral<B3Topic.Factory>() {
        })).to(Key.get(new TypeLiteral<StandardB3TopicFactory>() {
        }));

        bind(Key.get(new TypeLiteral<AgentProxy<GenericB3Message>>() {
        })).to(Key.get(new TypeLiteral<ReadOnlyMessageProviderAgentProxy<GenericB3Message>>() {
        }));
        bind(B3ITopicFactoryProxy.class).to(PassthroughITopicFactoryProxy.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    Agent.Config buildConfig() {
        return new Config() {

            @Override
            public String agentId() {
                return "prova";
            }
            
        };
    }
}
