package io.tinga.b3.groupagent.processors;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;

import io.tinga.b3.core.shadowing.ShadowDesiredPreProcessor;
import io.tinga.b3.groupagent.GroupAgentConfig;
import io.tinga.b3.protocol.GenericMessage;

public class EdgeGroupDesiredPreProcessor implements ShadowDesiredPreProcessor<GenericMessage> {

    private final GroupAgentConfig config;

    @Inject
    public EdgeGroupDesiredPreProcessor(GroupAgentConfig config) {
        this.config = config;
    }

    @Override
    public void inPlaceProcess(GenericMessage incomingDesired) {
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
