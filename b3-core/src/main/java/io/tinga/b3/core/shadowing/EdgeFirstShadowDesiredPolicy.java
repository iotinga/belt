package io.tinga.b3.core.shadowing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

import io.tinga.b3.core.Agent;
import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.protocol.GenericMessage;
import io.tinga.b3.protocol.topic.AgentTopic;
import it.netgrid.bauer.Topic;

public class EdgeFirstShadowDesiredPolicy implements Agent.ShadowDesiredPolicy<ObjectNode, GenericMessage> {

    private static final Logger log = LoggerFactory.getLogger(EdgeFirstShadowDesiredPolicy.class);

    private final VersionSafeExecutor executor;
    private final EdgeDriver<ObjectNode, GenericMessage> fieldDriver;
    private final ITopicFactoryProxy topicFactory;

    private Topic<GenericMessage> topic;

    @Inject
    public EdgeFirstShadowDesiredPolicy(VersionSafeExecutor executor, EdgeDriver<ObjectNode, GenericMessage> fieldDriver,
            ITopicFactoryProxy topicFactory) {
        this.executor = executor;
        this.fieldDriver = fieldDriver;
        this.topicFactory = topicFactory;
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
            if (event.getVersion() != Agent.VERSION_WILDCARD && version.apply(false) != event.getVersion()) {
                log.info(String.format("Refusing desired update: wildcard(%d) desired(%d) current(%d)",
                        Agent.VERSION_WILDCARD, event.getVersion(), version.apply(false)));
                return null;
            }

            this.fieldDriver.write(event);

            return null;
        });

        return true;
    }

    @Override
    public void bindTo(AgentTopic agent, String roleName) {
        this.topic = this.topicFactory.getTopic(agent.shadow().desired("#"), false);
        this.topic.addHandler(this);
    }

}
