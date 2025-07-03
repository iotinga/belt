package io.tinga.b3.groupagent.driver.states;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.tinga.b3.core.AgentProxy;
import io.tinga.b3.core.driver.AbstractFsmEdgeDriver;
import io.tinga.b3.core.driver.ConnectionState;
import io.tinga.b3.core.driver.AbstractFsmEdgeDriver.Context;
import io.tinga.b3.core.shadowing.ShadowDesiredPreProcessor;
import io.tinga.b3.core.shadowing.ShadowReportedPostProcessor;
import io.tinga.b3.groupagent.GroupAgentConfig;
import io.tinga.b3.groupagent.driver.GroupAgentEdgeDriver;
import io.tinga.b3.protocol.GenericB3Message;
import io.tinga.b3.protocol.topic.B3Topic;
import io.tinga.b3.protocol.topic.B3TopicRoot;
import io.tinga.belt.output.Status;
import it.netgrid.bauer.EventHandler;

@Singleton
public class EdgeDriverFsmConnected
        implements AbstractFsmEdgeDriver.State<EdgeDriverFsmState, GenericB3Message> {

    private static final Logger log = LoggerFactory.getLogger(GroupAgentEdgeDriver.class);

    private final ShadowDesiredPreProcessor<GenericB3Message> desiredPreProcessor;
    private final ShadowReportedPostProcessor<GenericB3Message> reportedPostProcessor;
    private final GroupAgentConfig config;
    private final AgentProxy.Factory factory;
    private final B3TopicRoot topicsRoot;
    private final ObjectNode currentShadowReported;

    private final Map<B3Topic, AgentProxy<GenericB3Message>> membersProxies = new HashMap<>();
    private final Map<String, B3Topic> fragNameMember = new HashMap<>();

    private ConnectionState connectionState;
    private Context<GenericB3Message> contextOnEnterState;

    @Inject
    public EdgeDriverFsmConnected(GroupAgentConfig config,
            AgentProxy.Factory factory, B3TopicRoot topicsRoot, ObjectMapper om,
            ShadowDesiredPreProcessor<GenericB3Message> desiredPreProc,
            ShadowReportedPostProcessor<GenericB3Message> reportedPostProc) {
        this.connectionState = ConnectionState.CONNECTING;
        this.config = config;
        this.factory = factory;
        this.topicsRoot = topicsRoot;
        this.currentShadowReported = om.createObjectNode();
        this.desiredPreProcessor = desiredPreProc;
        this.reportedPostProcessor = reportedPostProc;
    }

    @Override
    public ConnectionState getConnectionState() {
        return this.connectionState;
    }

    @Override
    public EdgeDriverFsmState onConnectDelta(Context<GenericB3Message> context) {
        return this.current();
    }

    @Override
    public EdgeDriverFsmState onDisconnectDelta(
            Context<GenericB3Message> context) {
        return EdgeDriverFsmState.DISCONNECTED;
    }

    @Override
    public EdgeDriverFsmState onWriteDelta(
            Context<GenericB3Message> context) {
        if (this.getConnectionState() != ConnectionState.CONNECTED)
            return this.current();

        if (context.incomingDesired() != null) {
            this.write(context.incomingDesired());
        }

        return this.current();
    }

    @Override
    public EdgeDriverFsmState onEmitDelta(Context<GenericB3Message> context) {
        return this.current();
    }

    @Override
    public synchronized void enter(Context<GenericB3Message> context) {
        this.connectionState = ConnectionState.CONNECTING;
        this.contextOnEnterState = context;

        this.desiredPreProcessor.initialize();
        this.reportedPostProcessor.initialize();

        if (membersProxies.size() < 1)
            for (String memberAgentId : config.members()) {
                final EdgeDriverFsmConnected fsmState = this;
                final B3Topic member = topicsRoot.agent(memberAgentId);
                AgentProxy<GenericB3Message> memberProxy = factory.getProxy(member,
                        config.roleInMembers());
                memberProxy.subscribe(new EventHandler<GenericB3Message>() {

                    @Override
                    public String getName() {
                        return fsmState.getClass().getName();
                    }

                    @Override
                    public Class<GenericB3Message> getEventClass() {
                        return GenericB3Message.class;
                    }

                    @Override
                    public boolean handle(String topic, GenericB3Message event) throws Exception {
                        fsmState.updateShadowReported(member, event);
                        return true;
                    }

                });
                this.membersProxies.put(member, memberProxy);
                this.fragNameMember.put(this.getFragmentNameFor(member), member);
            }

        this.connectionState = ConnectionState.CONNECTED;
    }

    @Override
    public synchronized void exit(Context<GenericB3Message> context) {
        this.connectionState = ConnectionState.DISCONNECTING;
        this.contextOnEnterState = null;
        this.connectionState = ConnectionState.DISCONNECTED;
    }

    private synchronized void updateShadowReported(B3Topic topicName, GenericB3Message message) {
        Context<GenericB3Message> context = this.contextOnEnterState;
        if (context != null) {
            String shadowSectionName = this.getFragmentNameFor(topicName);
            this.currentShadowReported.set(shadowSectionName, message.getBody());
            ObjectNode shadowReportedCopy = this.currentShadowReported.deepCopy();
            GenericB3Message newShadowMessage = new GenericB3Message(null, 0, 0, null, Status.ACCEPTED,
                    shadowReportedCopy);
            this.reportedPostProcessor.inPlaceProcess(newShadowMessage);
            context.reportedEmitter().apply(newShadowMessage);
        }
    }

    private void write(GenericB3Message desiredMessage) {
        if (desiredMessage == null || !(desiredMessage.getBody() instanceof ObjectNode)) {
            log.error(String.format("Invalid shadow desired message: %s",
                    desiredMessage == null ? "desiredMessage is null"
                            : "desiredMessage body isn't an ObjectNode instance"));
            return;
        }
        this.desiredPreProcessor.inPlaceProcess(desiredMessage);
        ObjectNode newShadowDesired = (ObjectNode) desiredMessage.getBody();
        Iterator<String> keys = newShadowDesired.fieldNames();
        while (keys.hasNext()) {
            String key = keys.next();

            B3Topic member = this.fragNameMember.get(key);
            if (member != null) {
                ObjectNode memberFragment = newShadowDesired.get(key).deepCopy();
                GenericB3Message memberDesiredMessage = new GenericB3Message(desiredMessage.getTimestamp(),
                        desiredMessage.getVersion(), desiredMessage.getProtocolVersion(), desiredMessage.getCorrelationId(),
                        desiredMessage.getStatus(),
                        memberFragment);
                this.membersProxies.get(member).write(memberDesiredMessage);
            }
        }
    }

    /** Support Methods */
    private String getFragmentNameFor(B3Topic topicName) {
        return topicName.getId();
    }

    @Override
    public EdgeDriverFsmState current() {
        return EdgeDriverFsmState.CONNECTED;
    }

}
