package io.tinga.b3.core.shadowing;

import com.google.inject.Inject;

import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.ITopicFactoryProxy;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.protocol.GenericB3Message;

public class GenericEdgeFirstShadowDesiredPolicy extends AbstractEdgeFirstShadowDesiredPolicy<GenericB3Message> {

    @Inject
    public GenericEdgeFirstShadowDesiredPolicy(VersionSafeExecutor executor, EdgeDriver<GenericB3Message> edgeDriver,
            ITopicFactoryProxy topicFactory) {
        super(executor, edgeDriver, topicFactory);
    }

    @Override
    public Class<GenericB3Message> getEventClass() {
        return GenericB3Message.class;
    }
    
}
