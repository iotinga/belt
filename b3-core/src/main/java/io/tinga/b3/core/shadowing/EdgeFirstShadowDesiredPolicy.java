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
import io.tinga.b3.protocol.topic.B3Topic;
import io.tinga.belt.helpers.AEventHandler;
import it.netgrid.bauer.Topic;

public class EdgeFirstShadowDesiredPolicy<M extends B3Message<?>> extends AEventHandler<M>
        implements Agent.ShadowDesiredPolicy<M> {

    private static final Logger log = LoggerFactory.getLogger(EdgeFirstShadowDesiredPolicy.class);

    protected final VersionSafeExecutor executor;
    protected final EdgeDriver<M> edgeDriver;
    protected final ITopicFactoryProxy topicFactory;

    protected Topic<M> topic;

    @Inject
    public EdgeFirstShadowDesiredPolicy(Class<M> eventClass, VersionSafeExecutor executor, EdgeDriver<M> edgeDriver,
            ITopicFactoryProxy topicFactory) {
        super(eventClass);
        this.executor = executor;
        this.edgeDriver = edgeDriver;
        this.topicFactory = topicFactory;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean handle(String topicName, M event) throws Exception {
        this.executor.safeExecute(version -> {
            if (hasConflicts(version, event)) {
                log.info(String.format("Refusing desired update: wildcard(%d) desired(%d) current(%d)",
                        Agent.VERSION_WILDCARD, event.getVersion(), version.apply(false)));
                return null;
            }

            try {
                this.edgeDriver.write(event);
            } catch (EdgeDriverException e) {
                log.warn(e.getMessage());
            }

            return null;
        });

        return true;
    }

    public boolean hasConflicts(Function<Boolean, Integer> version, M event) {
        return !event.getVersion().equals(Agent.VERSION_WILDCARD) && !version.apply(false).equals(event.getVersion());
    }

    @Override
    public void bindTo(B3Topic topicName, String roleName) {
        this.topic = this.topicFactory.getTopic(topicName.shadow().desired("#"), false);
        this.topic.addHandler(this);
    }

}
