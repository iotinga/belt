package io.tinga.b3.core.shadowing.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.tinga.b3.core.Agent;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.core.shadowing.VersionSafeExecutor;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3Topic;
import io.tinga.b3.protocol.topic.B3TopicRoot;
import it.netgrid.bauer.Topic;

public class EdgeFirstShadowReportedPolicy<M extends B3Message<?>>
        implements Agent.ShadowReportedPolicy<M> {

    private static final Logger log = LoggerFactory.getLogger(EdgeFirstShadowReportedPolicy.class);

    protected final VersionSafeExecutor executor;
    protected final Agent.EdgeDriver<M> edgeDriver;
    protected final ITopicFactoryProxy topicFactory;

    private Topic<M> topic;

    private M lastSentMessage;

    @Inject
    public EdgeFirstShadowReportedPolicy(VersionSafeExecutor executor, Agent.EdgeDriver<M> edgeDriver,
            ITopicFactoryProxy topicFactory) {
        this.executor = executor;
        this.edgeDriver = edgeDriver;
        this.topicFactory = topicFactory;
    }

    @Override
    public void bindTo(B3TopicRoot topicRoot, String roleName) {
        this.topic = this.topicFactory.getTopic(topicRoot.shadow().reported(), true);
        this.edgeDriver.subscribe(this);
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean handle(B3Topic topic, M event) throws Exception {
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

        // try {
        //     Operation operation = operationFactory.buildFrom(topic, event);
        //     boolean result = checker.isAllowed(operation);
        //     if (result) {
        //         out.put(String.format("[ ALLOW]: %s %s@%s -> %s", operation.message().getAction().name(),
        //                 operation.role(),
        //                 operation.desiredTopic(), operation.reportedTopic()));
        //         this.topic.post(event);
        //         return true;
        //     } else {
        //         out.put(String.format("[REJECT]: %s %s@%s -> %s", operation.message().getAction().name(),
        //                 operation.role(),
        //                 operation.desiredTopic(), operation.reportedTopic()));
        //         return false;
        //     }
        // } catch (EdgeDriverException e) {
        //     log.warn(e.getMessage());
        //     return false;
        // } catch (InvalidOperationException e) {
        //     log.warn(e.getMessage());
        //     return false;
        // }

    protected Topic<M> getTopic() {
        return topic;
    }

    protected M getLastSentMessage() {
        return lastSentMessage;
    }

}
