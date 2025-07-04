package io.tinga.b3.core.shadowing.impl;

import com.google.inject.Inject;

import io.tinga.b3.core.InitializationException;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3TopicRoot;
import it.netgrid.bauer.EventHandler;
import it.netgrid.bauer.ITopicFactory;
import it.netgrid.bauer.Topic;

/**
 * This VersionSafeExecutor requires always a fresh topic creation from the
 * ITopicFactory without the retain
 * flag, as it doesn't need to write to the topic reported.
 */
public class InitFromReportedTopicVersionSafeExecutor<M extends B3Message<?>> extends AbstracVersionSafeExecutor
        implements EventHandler<M> {

    private final ITopicFactory topicFactory;
    private final Class<M> eventClass;
    private B3TopicRoot topicRoot;
    private Topic<M> reportedTopic;

    @Inject
    public InitFromReportedTopicVersionSafeExecutor(Class<M> eventClass, ITopicFactory topicFactory) {
        this.topicFactory = topicFactory;
        this.eventClass = eventClass;
    }

    @Override
    public void initVersion(B3TopicRoot topicRoot) throws InitializationException {
        try {
            this.topicRoot = topicRoot;
            this.reportedTopic = this.topicFactory.getTopic(this.topicRoot.shadow().reported().build().toString());
            this.reportedTopic.addHandler(this);
        } catch (Exception e) {
            throw new InitializationException(e.getMessage());
        }
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public Class<M> getEventClass() {
        return this.eventClass;
    }

    @Override
    public boolean handle(String topicRoot, M event) throws Exception {
        if (!this.isInitialized()) {
            this.initCurrentReportedVersion(event.getVersion());
        }
        return true;
    }

}
