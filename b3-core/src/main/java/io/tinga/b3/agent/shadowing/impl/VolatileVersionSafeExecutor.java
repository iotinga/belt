package io.tinga.b3.agent.shadowing.impl;

import io.tinga.b3.agent.InitializationException;
import io.tinga.b3.protocol.B3Topic;

/**
 * This VersionSafeExecutor requires always a fresh topic creation from the ITopicFactory without the retain
 * flag, as it doesn't need to write to the topic reported.
 */
public class VolatileVersionSafeExecutor extends AbstracVersionSafeExecutor {

    @Override
    public void initVersion(B3Topic.Base topicBase) throws InitializationException {
        if (!this.isInitialized()) {
            this.initCurrentReportedVersion(1);
        }
    }


}
