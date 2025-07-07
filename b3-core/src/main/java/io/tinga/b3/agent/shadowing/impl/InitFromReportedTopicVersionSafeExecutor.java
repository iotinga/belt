package io.tinga.b3.agent.shadowing.impl;

import com.google.inject.Inject;

import io.tinga.b3.agent.InitializationException;
import io.tinga.b3.helpers.AgentProxy;
import io.tinga.b3.protocol.B3EventHandler;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;

/**
 * This VersionSafeExecutor requires always a fresh topic creation from the
 * ITopicFactory without the retain
 * flag, as it doesn't need to write to the topic reported.
 */
public class InitFromReportedTopicVersionSafeExecutor<M extends B3Message<?>> extends AbstracVersionSafeExecutor
        implements B3EventHandler<M> {

    private final AgentProxy.Factory agentProxyFactory;
    private AgentProxy<M> agentProxy;

    @Inject
    public InitFromReportedTopicVersionSafeExecutor(AgentProxy.Factory agentProxyFactory) {
        this.agentProxyFactory = agentProxyFactory;
    }

    @Override
    public void bind(B3Topic.Base topicBase, String roleName) throws InitializationException {
        try {
            this.agentProxy = this.agentProxyFactory.getProxy(topicBase, roleName);
            this.agentProxy.subscribe(this);
        } catch (Exception e) {
            throw new InitializationException(e.getMessage());
        }
    }

    @Override
    public String getName() {
        return InitFromReportedTopicVersionSafeExecutor.class.getName();
    }

    @Override
    public boolean handle(B3Topic topic, M event) throws Exception {
        if (!this.isInitialized()) {
            this.agentProxy.unsubscribe(this);
            this.agentProxy = null;
            this.initCurrentReportedVersion(event.getVersion());
        }
        return true;
    }

}
