package io.tinga.b3.groupagent.states;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.tinga.b3.core.AgentProxy;
import io.tinga.b3.core.connection.ConnectionState;
import io.tinga.b3.core.impl.AbstractFsmEdgeDriver;
import io.tinga.b3.core.impl.AbstractFsmEdgeDriver.Context;
import io.tinga.b3.core.shadowing.ShadowDesiredPreProcessor;
import io.tinga.b3.core.shadowing.ShadowReportedPostProcessor;
import io.tinga.b3.groupagent.GroupAgentConfig;
import io.tinga.b3.groupagent.GroupAgentEdgeDriver;
import io.tinga.b3.protocol.Action;
import io.tinga.b3.protocol.GenericMessage;
import io.tinga.b3.protocol.topic.AgentTopic;
import io.tinga.b3.protocol.topic.RootTopic;
import io.tinga.belt.output.Status;
import it.netgrid.bauer.EventHandler;

@Singleton
public class EdgeDriverFsmConnected
        implements AbstractFsmEdgeDriver.State<EdgeDriverFsmState, JsonNode, GenericMessage> {

    private static final Logger log = LoggerFactory.getLogger(GroupAgentEdgeDriver.class);

    private final ShadowDesiredPreProcessor<GenericMessage> desiredPreProcessor;
    private final ShadowReportedPostProcessor<GenericMessage> reportedPostProcessor;
    private final GroupAgentConfig config;
    private final AgentProxy.Factory factory;
    private final RootTopic topicsRoot;
    private final ObjectNode currentShadowReported;

    private final Map<AgentTopic, AgentProxy<JsonNode, GenericMessage>> membersProxies = new HashMap<>();
    private final Map<String, AgentTopic> fragNameMember = new HashMap<>();

    private ConnectionState connectionState;
    private Context<GenericMessage> contextOnEnterState;

    @Inject
    public EdgeDriverFsmConnected(GroupAgentConfig config,
            AgentProxy.Factory factory, RootTopic topicsRoot, ObjectMapper om,
            ShadowDesiredPreProcessor<GenericMessage> desiredPreProc,
            ShadowReportedPostProcessor<GenericMessage> reportedPostProc) {
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
    public EdgeDriverFsmState onConnectDelta(Context<GenericMessage> context) {
        return this.current();
    }

    @Override
    public EdgeDriverFsmState onDisconnectDelta(
            Context<GenericMessage> context) {
        return EdgeDriverFsmState.DISCONNECTED;
    }

    @Override
    public EdgeDriverFsmState onWriteDelta(
            Context<GenericMessage> context) {
        if (this.getConnectionState() != ConnectionState.CONNECTED)
            return this.current();

        if (context.incomingDesired() != null) {
            this.write(context.incomingDesired());
        }

        return this.current();
    }

    @Override
    public EdgeDriverFsmState onEmitDelta(Context<GenericMessage> context) {
        return this.current();
    }

    @Override
    public synchronized void enter(Context<GenericMessage> context) {
        this.connectionState = ConnectionState.CONNECTING;
        this.contextOnEnterState = context;

        this.desiredPreProcessor.initialize();
        this.reportedPostProcessor.initialize();

        if (membersProxies.size() < 1)
            for (String memberAgentId : config.members()) {
                final EdgeDriverFsmConnected fsmState = this;
                final AgentTopic member = topicsRoot.agent(memberAgentId);
                AgentProxy<JsonNode, GenericMessage> memberProxy = factory.getProxy(member,
                        config.roleInMembers());
                memberProxy.subscribe(new EventHandler<GenericMessage>() {

                    @Override
                    public String getName() {
                        return fsmState.getClass().getName();
                    }

                    @Override
                    public Class<GenericMessage> getEventClass() {
                        return GenericMessage.class;
                    }

                    @Override
                    public boolean handle(String topic, GenericMessage event) throws Exception {
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
    public synchronized void exit(Context<GenericMessage> context) {
        this.connectionState = ConnectionState.DISCONNECTING;
        this.contextOnEnterState = null;
        this.connectionState = ConnectionState.DISCONNECTED;
    }

    private synchronized void updateShadowReported(AgentTopic agent, GenericMessage message) {
        Context<GenericMessage> context = this.contextOnEnterState;
        if (context != null) {
            String shadowSectionName = this.getFragmentNameFor(agent);
            this.currentShadowReported.set(shadowSectionName, message.getBody());
            JsonNode shadowReportedCopy = this.currentShadowReported.deepCopy();
            GenericMessage newShadowMessage = new GenericMessage(null, 0, 0, Action.PUT, Status.ACCEPTED,
                    shadowReportedCopy);
            this.reportedPostProcessor.inPlaceProcess(newShadowMessage);
            context.reportedEmitter().apply(newShadowMessage);
        }
    }

    private void write(GenericMessage desiredMessage) {
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

            AgentTopic member = this.fragNameMember.get(key);
            if (member != null) {
                JsonNode memberFragment = newShadowDesired.get(key).deepCopy();
                GenericMessage memberDesiredMessage = new GenericMessage(desiredMessage.getTimestamp(),
                        desiredMessage.getVersion(), desiredMessage.getProtocolVersion(), desiredMessage.getAction(),
                        desiredMessage.getStatus(),
                        memberFragment);
                this.membersProxies.get(member).write(memberDesiredMessage);
            }
        }
    }

    /** Support Methods */
    private String getFragmentNameFor(AgentTopic agentTopic) {
        return agentTopic.getId();
    }

    @Override
    public EdgeDriverFsmState current() {
        return EdgeDriverFsmState.CONNECTED;
    }

}
