package io.tinga.b3.core.impl;

import io.tinga.b3.core.AgentInitException;
import io.tinga.b3.protocol.topic.AgentTopic;

/**
 * This VersionSafeExecutor requires always a fresh topic creation from the ITopicFactory without the retain
 * flag, as it doesn't need to write to the topic reported.
 */
public class VolatileVersionSafeExecutor extends AbstracVersionSafeExecutor {

    @Override
    public void initVersion(AgentTopic agentTopic) throws AgentInitException {
        if (!this.isInitialized()) {
            this.initCurrentReportedVersion(1);
        }
    }


}
