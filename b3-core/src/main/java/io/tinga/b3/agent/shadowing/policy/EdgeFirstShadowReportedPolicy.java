package io.tinga.b3.agent.shadowing.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.agent.Agent;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor;
import io.tinga.b3.helpers.AgentProxy;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.B3ITopicFactoryProxy;
import it.netgrid.bauer.Topic;

public class EdgeFirstShadowReportedPolicy<M extends B3Message<?>>
        implements Agent.ShadowReportedPolicy<M> {

    private static final Logger log = LoggerFactory.getLogger(EdgeFirstShadowReportedPolicy.class);

    protected final VersionSafeExecutor executor;
    protected final Agent.EdgeDriver<M> edgeDriver;

    private final AgentProxy.Factory agentProxyFactory;
    private final B3ITopicFactoryProxy topicFactory;
    private AgentProxy<M> agentProxy;

    private B3Topic.Base boundTopicBase;
    private Topic<M> topic;

    private M lastSentMessage;

    @Inject
    public EdgeFirstShadowReportedPolicy(VersionSafeExecutor executor, Agent.EdgeDriver<M> edgeDriver,
            AgentProxy.Factory agentProxyFactory, B3ITopicFactoryProxy topicFactory) {
        this.executor = executor;
        this.edgeDriver = edgeDriver;
        this.agentProxyFactory = agentProxyFactory;
        this.topicFactory = topicFactory;
    }

    @Override
    public void bind(B3Topic.Base topicBase, String roleName) {
        this.boundTopicBase = topicBase;
        this.agentProxy = this.agentProxyFactory.getProxy(topicBase, roleName);
        this.agentProxy.subscribe(this);
    }

    @Override
    public String getName() {
        return EdgeFirstShadowReportedPolicy.class.getName();
    }

    @Override
    public boolean handle(B3Topic topic, M event) throws Exception {
        if (lastSentMessage == null) {
            lastSentMessage = event;
            this.agentProxy.unsubscribe(this);
            this.topic = this.topicFactory.getTopic(this.boundTopicBase.shadow().reported().build(), true);
            this.edgeDriver.subscribe(this);
        } else {
            this.executor.safeExecute(version -> {
                if (!lastSentMessage.equals(event)) {
                    int messageVersion = event.getVersion();
                    event.setVersion(version.apply(true));
                    this.topic.post(event);
                    lastSentMessage = event;
                    log.info(String.format(
                            "New reported published: wildcard(%d) messageVersion(%d) publishedVersion(%d)",
                            Agent.VERSION_WILDCARD, messageVersion, event.getVersion()));
                }
                return null;
            });
        }

        return true;
    }

    protected Topic<M> getTopic() {
        return topic;
    }

    protected M getLastSentMessage() {
        return lastSentMessage;
    }

}
