package io.tinga.b3.agent.driver;

import java.util.Map;
import com.google.inject.Inject;

import io.tinga.b3.agent.GroupAgentConfig;
import io.tinga.b3.agent.driver.impl.AbstractFsmEdgeDriver;
import io.tinga.b3.agent.driver.states.EdgeDriverFsmConnected;
import io.tinga.b3.agent.driver.states.EdgeDriverFsmDisconnected;
import io.tinga.b3.agent.driver.states.EdgeDriverFsmState;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.protocol.B3Topic;

public class GroupAgentEdgeDriver
        extends AbstractFsmEdgeDriver<EdgeDriverFsmState, GenericB3Message> {

    private final Map<EdgeDriverFsmState, State<EdgeDriverFsmState, GenericB3Message>> stateMap;

    @Inject
    public GroupAgentEdgeDriver(GroupAgentConfig config, B3Topic.Factory topicFactory, EdgeDriverFsmConnected connected,
            EdgeDriverFsmDisconnected disconnected) {
        super(topicFactory.agent(config.agentId()), disconnected);
        this.stateMap = Map.of(
            EdgeDriverFsmState.CONNECTED, connected,
            EdgeDriverFsmState.DISCONNECTED, disconnected
        );
    }

    @Override
    protected State<EdgeDriverFsmState, GenericB3Message> get(EdgeDriverFsmState state) {
        return this.stateMap.get(state);
    }

}
