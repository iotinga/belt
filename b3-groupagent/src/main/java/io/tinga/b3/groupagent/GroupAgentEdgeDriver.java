package io.tinga.b3.groupagent;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import io.tinga.b3.core.impl.AbstractFsmEdgeDriver;
import io.tinga.b3.groupagent.states.EdgeDriverFsmConnected;
import io.tinga.b3.groupagent.states.EdgeDriverFsmDisconnected;
import io.tinga.b3.groupagent.states.EdgeDriverFsmState;
import io.tinga.b3.protocol.GenericMessage;
import io.tinga.b3.protocol.topic.RootTopic;

public class GroupAgentEdgeDriver
        extends AbstractFsmEdgeDriver<EdgeDriverFsmState, JsonNode, GenericMessage> {

    private final Map<EdgeDriverFsmState, State<EdgeDriverFsmState, JsonNode, GenericMessage>> stateMap;

    @Inject
    public GroupAgentEdgeDriver(GroupAgentCommand command, RootTopic rootTopic, EdgeDriverFsmConnected connected,
            EdgeDriverFsmDisconnected disconnected) {
        super(rootTopic.agent(command.agentId()));
        this.stateMap = Map.of(
            EdgeDriverFsmState.CONNECTED, connected,
            EdgeDriverFsmState.DISCONNECTED, disconnected
        );
    }

    @Override
    public State<EdgeDriverFsmState, JsonNode, GenericMessage> buildInitialState() {
        return this.stateMap.get(EdgeDriverFsmState.DISCONNECTED);
    }

    @Override
    protected State<EdgeDriverFsmState, JsonNode, GenericMessage> get(EdgeDriverFsmState state) {
        return this.stateMap.get(state);
    }

}
