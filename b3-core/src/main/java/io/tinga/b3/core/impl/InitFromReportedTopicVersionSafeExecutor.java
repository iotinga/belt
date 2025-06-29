package io.tinga.b3.core.impl;

import com.google.inject.Inject;

import io.tinga.b3.core.AgentInitException;
import io.tinga.b3.protocol.GenericMessage;
import io.tinga.b3.protocol.topic.AgentTopic;
import it.netgrid.bauer.EventHandler;
import it.netgrid.bauer.ITopicFactory;
import it.netgrid.bauer.Topic;

/**
 * This VersionSafeExecutor requires always a fresh topic creation from the ITopicFactory without the retain
 * flag, as it doesn't need to write to the topic reported.
 */
public class InitFromReportedTopicVersionSafeExecutor extends AbstracVersionSafeExecutor implements EventHandler<GenericMessage> {

    private final ITopicFactory topicFactory;
    private AgentTopic agentTopic;
    private Topic<GenericMessage> reportedTopic;

    @Inject
    public InitFromReportedTopicVersionSafeExecutor(ITopicFactory topicFactory) {
        this.topicFactory = topicFactory;
    }

    @Override
    public void initVersion(AgentTopic agentTopic) throws AgentInitException {
        try{
            this.agentTopic = agentTopic;
            this.reportedTopic = this.topicFactory.getTopic(this.agentTopic.shadow().reported().build());
            this.reportedTopic.addHandler(this);
        } catch(Exception e) {
            throw new AgentInitException(e.getMessage());
        }
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
        if (!this.isInitialized()) {
            this.initCurrentReportedVersion(event.getVersion());
        }
        return true;
    }


}
