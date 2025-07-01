package io.tinga.b3.core.shadowing;

import com.google.inject.Inject;

import io.tinga.b3.core.Agent;
import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3Topic;
import io.tinga.belt.output.GadgetSink;
import it.netgrid.bauer.Topic;

public abstract class SinkShadowReportedPolicy<M extends B3Message<?>> implements Agent.ShadowReportedPolicy<M> {

    private final GadgetSink out;
    protected Topic<M> topic;
    protected M lastSentMessage;

    protected final EdgeDriver<M> fieldDriver;

    @Inject
    public SinkShadowReportedPolicy(GadgetSink out, EdgeDriver<M> fieldDriver) {
        this.fieldDriver = fieldDriver;
        this.out = out;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean handle(String topic, M event) throws Exception {
        this.out.put(String.format("%s -> %s", topic, event));
        return true;
    }

    @Override
    public void bindTo(B3Topic topicName, String roleName) {
        this.fieldDriver.subscribe(this);
    }
    
}
