package io.tinga.b3.core.shadowing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import io.tinga.b3.core.Agent;
import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.protocol.GenericMessage;
import io.tinga.b3.protocol.topic.AgentTopic;
import it.netgrid.bauer.Topic;

public class EdgeFirstShadowReportedPolicy implements Agent.ShadowReportedPolicy<JsonNode, GenericMessage> {

    private static final Logger log = LoggerFactory.getLogger(EdgeFirstShadowReportedPolicy.class);

    private final VersionSafeExecutor executor;
    private final EdgeDriver<JsonNode, GenericMessage> fieldDriver;
    private final ITopicFactoryProxy topicFactory;

    private Topic<GenericMessage> topic;
    private GenericMessage lastSentMessage;

    @Inject
    public EdgeFirstShadowReportedPolicy(VersionSafeExecutor executor, EdgeDriver<JsonNode, GenericMessage> fieldDriver,
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
    public Class<GenericMessage> getEventClass() {
        return GenericMessage.class;
    }

    @Override
    public boolean handle(String topicName, GenericMessage event) throws Exception {
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
