package io.tinga.b3.agent.shadowing.impl;

import com.google.inject.Inject;

import io.tinga.b3.agent.InitializationException;
import io.tinga.b3.protocol.B3ITopicFactoryProxy;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;
import it.netgrid.bauer.EventHandler;
import it.netgrid.bauer.Topic;

/**
 * This VersionSafeExecutor requires always a fresh topic creation from the
 * ITopicFactory without the retain
 * flag, as it doesn't need to write to the topic reported.
 */
public class InitFromReportedTopicVersionSafeExecutor<M extends B3Message<?>> extends AbstracVersionSafeExecutor
        implements EventHandler<M> {

    private final B3ITopicFactoryProxy topicFactory;
    private final Class<M> eventClass;
    private B3Topic.Base topicBase;
    private Topic<M> reportedTopic;

    @Inject
    public InitFromReportedTopicVersionSafeExecutor(Class<M> eventClass, B3ITopicFactoryProxy topicFactory) {
        this.topicFactory = topicFactory;
        this.eventClass = eventClass;
    }

    @Override
    public void initVersion(B3Topic.Base topicBase) throws InitializationException {
        try {
            this.topicBase = topicBase;
            this.reportedTopic = this.topicFactory.getTopic(this.topicBase.shadow().reported().build(), false);
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
    public boolean handle(String topicBase, M event) throws Exception {
        if (!this.isInitialized()) {
            this.initCurrentReportedVersion(event.getVersion());
        }
        return true;
    }

}
