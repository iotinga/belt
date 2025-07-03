package io.tinga.b3.core.driver.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import io.tinga.b3.core.driver.AgentProxy;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3TopicRoot;

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
    public <M extends B3Message<?>> AgentProxy<M> getProxy(B3TopicRoot topicRoot, String roleName) {
        String cacheKey = this.buildCacheEntryKey(topicRoot, roleName);
        AgentProxy<M> cacheItem = (AgentProxy<M>) this.cache.get(cacheKey);
        if (cacheItem == null) {
            cacheItem = this.injector.getInstance(Key.get(new TypeLiteral<AgentProxy<M>>() {
            }));
            cacheItem.bindTo(topicRoot, roleName);
            this.cache.put(cacheKey, (AgentProxy<B3Message<?>>) cacheItem);
        }

        return cacheItem;
    }

    private String buildCacheEntryKey(B3TopicRoot topicRoot, String desiredRole) {
        return topicRoot.toString() + "_" + desiredRole;
    }

}
