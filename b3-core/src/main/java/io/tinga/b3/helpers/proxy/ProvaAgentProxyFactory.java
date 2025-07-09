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

import it.netgrid.bauer.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.tinga.b3.agent.Agent;
import io.tinga.b3.protocol.B3EventHandler;
import io.tinga.b3.protocol.B3ITopicFactoryProxy;
import io.tinga.belt.helpers.AEventHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProvaAgentProxyFactory<M extends B3Message<?>> implements AgentProxy.Factory<M> {

    private final Map<String, StaticTopicBasedAgentProxy<M>> cache;
    private final Injector injector;

    @Inject
    public ProvaAgentProxyFactory(Injector injector) {
        this.injector = injector;
        this.cache = new HashMap<>();
    }

    @Override
    public AgentProxy<M> getProxy(B3Topic.Base topicBase, String roleName) {
        String cacheKey = this.buildCacheEntryKey(topicBase, roleName);
        StaticTopicBasedAgentProxy<M> cacheItem = this.cache.get(cacheKey);
        if (cacheItem == null) {
            cacheItem = this.injector.getInstance(Key.get(new TypeLiteral<StaticTopicBasedAgentProxy<M>>() {
            }));
            this.cache.put(cacheKey, cacheItem);
        }

        return cacheItem;
    }

    private String buildCacheEntryKey(B3Topic.Base topicBase, String desiredRole) {
        return topicBase.toString() + "_" + desiredRole;
    }

    public class StaticTopicBasedAgentProxy<M2 extends B3Message<?>> extends AEventHandler<M2>
            implements B3EventHandler<M2>, AgentProxy<M2> {
        private static final Logger log = LoggerFactory.getLogger(StaticTopicBasedAgentProxy.class);

        private B3Topic.Base topicBase;
        private String roleName;
        private Topic<M2> desiredTopic;
        private Topic<M2> reportedTopic;
        protected final List<B3EventHandler<M2>> subscribers;
        private final B3ITopicFactoryProxy topicFactoryProxy;
        private final B3Topic.Factory topicFactory;
        protected final Agent.Config config;

        protected M2 lastShadowReported;

        @Inject
        public StaticTopicBasedAgentProxy(Agent.Config config,
                Class<M2> messageClass, B3ITopicFactoryProxy topicFactoryProxy, B3Topic.Factory topicFactory) {
            super(messageClass);
            this.config = config;
            this.topicFactory = topicFactory;
            this.topicFactoryProxy = topicFactoryProxy;
            this.subscribers = new CopyOnWriteArrayList<>();
        }

        @Override
        public String getName() {
            return String.format("%s-%s", config.agentId(), StaticTopicBasedAgentProxy.class.getSimpleName());
        }

        @Override
        public synchronized void bind(B3Topic.Base topicBase, String roleName) {
            if (desiredTopic == null && this.reportedTopic == null) {
                this.topicBase = topicBase;
                this.roleName = roleName;
                this.desiredTopic = topicFactoryProxy
                        .getTopic(topicBase.shadow().desired(roleName).build(), true);
                this.reportedTopic = topicFactoryProxy.getTopic(topicBase.shadow().reported().build(), false);
                this.reportedTopic.addHandler(this);
            }
        }

        @Override
        public boolean handle(String topicPath, M2 newShadowReported) throws Exception {
            B3Topic topic = this.topicFactory.parse(topicPath).build();
            return this.handle(topic, newShadowReported);
        }

        private synchronized boolean safeUpdateLastShadowReported(M2 newShadowReported) {
            if (lastShadowReported == null || lastShadowReported.getVersion() <= newShadowReported.getVersion()) {
                lastShadowReported = newShadowReported;
                return true;
            }
            return false;
        }

        @Override
        public B3Topic.Base getBoundTopicBase() {
            return this.topicBase;
        }

        @Override
        public String getBoundRoleName() {
            return this.roleName;
        }

        @Override
        public void subscribe(B3EventHandler<M2> observer) {
            subscribers.add(observer);
        }

        @Override
        public void unsubscribe(B3EventHandler<M2> observer) {
            subscribers.remove(observer);
        }

        @Override
        public synchronized void write(M2 desiredMessage) {
            if (this.desiredTopic == null) {
                log.error("Trying to write before bind: message ignored");
                return;
            }
            Integer currentVersion = this.lastShadowReported == null ? Agent.VERSION_WILDCARD
                    : this.lastShadowReported.getVersion();
            desiredMessage.setVersion(currentVersion);
            this.desiredTopic.post(desiredMessage);
        }

        @Override
        public boolean handle(B3Topic topic, M2 newShadowReported) throws Exception {
            if (this.safeUpdateLastShadowReported(newShadowReported)) {
                for (B3EventHandler<M2> listener : subscribers) {
                    try {
                        listener.handle(topic, newShadowReported);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
            }
            return true;
        }

    }

}
