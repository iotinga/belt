package io.tinga.b3.groupagent.driver.processors;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

import io.tinga.b3.core.driver.ShadowDesiredPreProcessor;
import io.tinga.b3.groupagent.GroupAgentConfig;
import io.tinga.b3.protocol.GenericB3Message;

public class EdgeGroupAgentDesiredPreProcessor implements ShadowDesiredPreProcessor<GenericB3Message> {

    private final GroupAgentConfig config;

    @Inject
    public EdgeGroupAgentDesiredPreProcessor(GroupAgentConfig config) {
        this.config = config;
    }

    @Override
    public void inPlaceProcess(GenericB3Message incomingDesired) {
        if (incomingDesired == null)
            return;

        ObjectNode shadow = (ObjectNode) incomingDesired.getBody();
        shadow.remove(this.config.agentId());
    }

    @Override
    public void initialize() {
        // NOTHING TO DO
    }

}
