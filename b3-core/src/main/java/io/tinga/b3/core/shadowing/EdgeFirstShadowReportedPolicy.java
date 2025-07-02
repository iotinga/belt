package io.tinga.b3.core.shadowing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.core.Agent;
import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3Topic;
import io.tinga.belt.helpers.AEventHandler;
import it.netgrid.bauer.Topic;

public class EdgeFirstShadowReportedPolicy<M extends B3Message<?>> extends AEventHandler<M>
        implements Agent.ShadowReportedPolicy<M> {

    private static final Logger log = LoggerFactory.getLogger(EdgeFirstShadowReportedPolicy.class);

    protected final VersionSafeExecutor executor;
    protected final EdgeDriver<M> edgeDriver;
    protected final ITopicFactoryProxy topicFactory;

    private Topic<M> topic;

    private M lastSentMessage;

    @Inject
    public EdgeFirstShadowReportedPolicy(Class<M> eventClass, VersionSafeExecutor executor, EdgeDriver<M> edgeDriver,
            ITopicFactoryProxy topicFactory) {
        super(eventClass);
        this.executor = executor;
        this.edgeDriver = edgeDriver;
        this.topicFactory = topicFactory;
    }

    @Override
    public void bindTo(B3Topic topicName, String roleName) {
        this.topic = this.topicFactory.getTopic(topicName.shadow().reported(), true);
        this.edgeDriver.subscribe(this);
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean handle(String topicName, M event) throws Exception {
        this.executor.safeExecute(version -> {
            if (lastSentMessage == null || !lastSentMessage.equals(event)) {
                int messageVersion = event.getVersion();
                event.setVersion(version.apply(true));
                this.topic.post(event);
                lastSentMessage = event;
                log.info(String.format("New reported published: wildcard(%d) messageVersion(%d) publishedVersion(%d)",
                        Agent.VERSION_WILDCARD, messageVersion, event.getVersion()));
            }
            return null;
        });

        return true;
    }

    protected Topic<M> getTopic() {
        return topic;
    }

    protected M getLastSentMessage() {
        return lastSentMessage;
    }

}
