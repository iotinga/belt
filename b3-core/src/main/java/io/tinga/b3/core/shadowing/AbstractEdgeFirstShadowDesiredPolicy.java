package io.tinga.b3.core.shadowing;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.core.Agent;
import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.EdgeDriverException;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.AgentTopic;
import it.netgrid.bauer.Topic;

public abstract class AbstractEdgeFirstShadowDesiredPolicy<M extends B3Message<?>> implements Agent.ShadowDesiredPolicy<M> {

    private static final Logger log = LoggerFactory.getLogger(AbstractEdgeFirstShadowDesiredPolicy.class);

    protected final VersionSafeExecutor executor;
    protected final EdgeDriver<M> fieldDriver;
    protected final ITopicFactoryProxy topicFactory;

    protected Topic<M> topic;

    @Inject
    public AbstractEdgeFirstShadowDesiredPolicy(VersionSafeExecutor executor, EdgeDriver<M> fieldDriver,
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
    public boolean handle(String topicName, M event) throws Exception {
        this.executor.safeExecute(version -> {
            if (hasNoConflicts(version, event)) {
                log.info(String.format("Refusing desired update: wildcard(%d) desired(%d) current(%d)",
                        Agent.VERSION_WILDCARD, event.getVersion(), version.apply(false)));
                return null;
            }

            try {
                this.fieldDriver.write(event);
            } catch (EdgeDriverException e) {
                log.warn(e.getMessage());
            }

            return null;
        });

        return true;
    }

    protected boolean hasNoConflicts(Function<Boolean, Integer> version, M event) {
        return event.getVersion() != Agent.VERSION_WILDCARD && version.apply(false) != event.getVersion();
    }

    @Override
    public void bindTo(AgentTopic agent, String roleName) {
        this.topic = this.topicFactory.getTopic(agent.shadow().desired("#"), false);
        this.topic.addHandler(this);
    }

}
