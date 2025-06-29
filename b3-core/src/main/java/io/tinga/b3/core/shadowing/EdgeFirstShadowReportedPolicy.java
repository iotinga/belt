package io.tinga.b3.core.shadowing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.core.Agent;
import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.protocol.GenericB3Message;
import io.tinga.b3.protocol.topic.AgentTopic;
import it.netgrid.bauer.Topic;

public class EdgeFirstShadowReportedPolicy implements Agent.ShadowReportedPolicy<GenericB3Message> {

    private static final Logger log = LoggerFactory.getLogger(EdgeFirstShadowReportedPolicy.class);

    private final VersionSafeExecutor executor;
    private final EdgeDriver<GenericB3Message> fieldDriver;
    private final ITopicFactoryProxy topicFactory;

    private Topic<GenericB3Message> topic;
    private GenericB3Message lastSentMessage;

    @Inject
    public EdgeFirstShadowReportedPolicy(VersionSafeExecutor executor, EdgeDriver<GenericB3Message> fieldDriver,
            ITopicFactoryProxy topicFactory) {
        this.executor = executor;
        this.fieldDriver = fieldDriver;
        this.topicFactory = topicFactory;
    }

    @Override
    public void bindTo(AgentTopic agent, String roleName) {
        this.topic = this.topicFactory.getTopic(agent.shadow().reported(), true);
        this.fieldDriver.subscribe(this);
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public Class<GenericB3Message> getEventClass() {
        return GenericB3Message.class;
    }

    @Override
    public boolean handle(String topicName, GenericB3Message event) throws Exception {
        this.executor.safeExecute(version -> {
            if (lastSentMessage == null || !lastSentMessage.equals(event)) {
                int messageVersion = event.getVersion();
                event.setVersion( version.apply(true));
                this.topic.post(event);
                log.info(String.format("New reported published: wildcard(%d) messageVersion(%d) publishedVersion(%d)",
                        Agent.VERSION_WILDCARD, messageVersion, event.getVersion()));
            }
            return null;
        });

        return true;
    }

}
