package io.tinga.b3.helpers.proxy;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import io.tinga.b3.helpers.AgentProxy;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;

public class CachedAgentProxyFactory<M extends B3Message<?>> implements AgentProxy.Factory<M> {

    private final Map<String, AgentProxy<M>> cache;
    private final Injector injector;
    private final TypeLiteral<AgentProxy<M>> proxyTypeLiteral;

    @Inject
    public CachedAgentProxyFactory(Injector injector, TypeLiteral<AgentProxy<M>> proxyTypeLiteral) {
        this.injector = injector;
        this.proxyTypeLiteral = proxyTypeLiteral;
        this.cache = new HashMap<>();
    }

    @Override
    public AgentProxy<M> getProxy(B3Topic.Base topicBase, String roleName) {
        String cacheKey = this.buildCacheEntryKey(topicBase, roleName);
        AgentProxy<M> cacheItem = (AgentProxy<M>) this.cache.get(cacheKey);
        if (cacheItem == null) {
            cacheItem = this.injector.getInstance(Key.get(this.proxyTypeLiteral));
            this.cache.put(cacheKey, cacheItem);
        }

        return cacheItem;
    }

    private String buildCacheEntryKey(B3Topic.Base topicBase, String desiredRole) {
        return topicBase.toString() + "_" + desiredRole;
    }

}
