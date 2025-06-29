package io.tinga.b3.core.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import io.tinga.b3.core.AgentProxy;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.AgentTopic;

public class AgentProxyFactoryImpl implements AgentProxy.Factory {

    private final Map<String, AgentProxy<B3Message<?>>> cache;
    private final Injector injector;

    @Inject
    public AgentProxyFactoryImpl(Injector injector) {
        this.injector = injector;
        this.cache = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <M extends B3Message<?>> AgentProxy<M> getProxy(AgentTopic agent, String roleName) {
        String cacheKey = this.buildCacheEntryKey(agent, roleName);
        AgentProxy<M> cacheItem = (AgentProxy<M>) this.cache.get(cacheKey);
        if (cacheItem == null) {
            cacheItem = this.injector.getInstance(Key.get(new TypeLiteral<AgentProxy<M>>() {
            }));
            cacheItem.bindTo(agent, roleName);
            this.cache.put(cacheKey, (AgentProxy<B3Message<?>>) cacheItem);
        }

        return cacheItem;
    }

    private String buildCacheEntryKey(AgentTopic agent, String desiredRole) {
        return agent.toString() + "_" + desiredRole;
    }

}
