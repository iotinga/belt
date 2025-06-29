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

public class EdgeFirstShadowDesiredPolicy implements Agent.ShadowDesiredPolicy<GenericB3Message> {

    private static final Logger log = LoggerFactory.getLogger(EdgeFirstShadowDesiredPolicy.class);

    private final VersionSafeExecutor executor;
    private final EdgeDriver<GenericB3Message> fieldDriver;
    private final ITopicFactoryProxy topicFactory;

    private Topic<GenericB3Message> topic;

    @Inject
    public EdgeFirstShadowDesiredPolicy(VersionSafeExecutor executor, EdgeDriver<GenericB3Message> fieldDriver,
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
    public Class<GenericB3Message> getEventClass() {
        return GenericB3Message.class;
    }

    @Override
    public boolean handle(String topicName, GenericB3Message event) throws Exception {
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
