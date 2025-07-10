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
public class RetainedReportedVersionSafeExecutor<M extends B3Message<?>> extends AbstracVersionSafeExecutor
        implements B3EventHandler<M> {

    private final AgentProxy<M> agentProxy;

    @Inject
    public RetainedReportedVersionSafeExecutor(AgentProxy<M> agentProxy) {
        this.agentProxy = agentProxy;
        this.agentProxy.subscribe(this);
    }

    @Override
    public void bind(B3Topic.Base topicBase, String roleName) throws InitializationException {
        // NOTHING TO DO
    }

    @Override
    public String getName() {
        return RetainedReportedVersionSafeExecutor.class.getSimpleName();
    }

    @Override
    public boolean handle(B3Topic topic, M event) throws Exception {
        if (!this.isInitialized()) {
            this.agentProxy.unsubscribe(this);
            this.initCurrentReportedVersion(event.getVersion());
        }
        return true;
    }

}
