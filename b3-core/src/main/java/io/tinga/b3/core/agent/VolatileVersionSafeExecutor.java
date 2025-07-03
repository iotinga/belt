package io.tinga.b3.core.agent;

import io.tinga.b3.core.AgentInitException;
import io.tinga.b3.protocol.topic.B3Topic;

/**
 * This VersionSafeExecutor requires always a fresh topic creation from the ITopicFactory without the retain
 * flag, as it doesn't need to write to the topic reported.
 */
public class VolatileVersionSafeExecutor extends AbstracVersionSafeExecutor {

    @Override
    public void initVersion(B3Topic topicRoot) throws AgentInitException {
        if (!this.isInitialized()) {
            this.initCurrentReportedVersion(1);
        }
    }


}
