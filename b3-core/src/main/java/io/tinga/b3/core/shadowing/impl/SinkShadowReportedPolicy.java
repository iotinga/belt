package io.tinga.b3.core.shadowing.impl;

import com.google.inject.Inject;

import io.tinga.b3.core.Agent;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.topic.B3Topic;
import io.tinga.b3.protocol.topic.B3TopicRoot;
import io.tinga.belt.output.GadgetSink;
import it.netgrid.bauer.Topic;

public class SinkShadowReportedPolicy<M extends B3Message<?>> implements Agent.ShadowReportedPolicy<M> {

    private final GadgetSink out;
    protected Topic<M> topic;
    protected M lastSentMessage;

    protected final Agent.EdgeDriver<M> edgeDriver;

    @Inject
    public SinkShadowReportedPolicy(GadgetSink out, Agent.EdgeDriver<M> edgeDriver) {
        this.edgeDriver = edgeDriver;
        this.out = out;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean handle(B3Topic topic, M event) throws Exception {
        this.out.put(String.format("%s -> %s", topic, event));
        return true;
    }

    @Override
    public void bindTo(B3TopicRoot topicRoot, String roleName) {
        this.edgeDriver.subscribe(this);
    }
    
}
