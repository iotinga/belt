package io.tinga.b3.groupagent.driver;

import java.util.Map;
import com.google.inject.Inject;

import io.tinga.b3.core.driver.impl.AbstractFsmEdgeDriver;
import io.tinga.b3.groupagent.GroupAgentConfig;
import io.tinga.b3.groupagent.driver.states.EdgeDriverFsmConnected;
import io.tinga.b3.groupagent.driver.states.EdgeDriverFsmDisconnected;
import io.tinga.b3.groupagent.driver.states.EdgeDriverFsmState;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.impl.GenericB3Message;

public class GroupAgentEdgeDriver
        extends AbstractFsmEdgeDriver<EdgeDriverFsmState, GenericB3Message> {

    private final Map<EdgeDriverFsmState, State<EdgeDriverFsmState, GenericB3Message>> stateMap;

    @Inject
    public GroupAgentEdgeDriver(GroupAgentConfig config, B3Topic.Factory topicFactory, EdgeDriverFsmConnected connected,
            EdgeDriverFsmDisconnected disconnected) {
        super(topicFactory.agent(config.agentId()));
        this.stateMap = Map.of(
            EdgeDriverFsmState.CONNECTED, connected,
            EdgeDriverFsmState.DISCONNECTED, disconnected
        );
    }

    @Override
    public State<EdgeDriverFsmState, GenericB3Message> buildInitialState() {
        return this.stateMap.get(EdgeDriverFsmState.DISCONNECTED);
    }

    @Override
    protected State<EdgeDriverFsmState, GenericB3Message> get(EdgeDriverFsmState state) {
        return this.stateMap.get(state);
    }

}
